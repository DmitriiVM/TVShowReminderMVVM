package com.example.tvshowreminder.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.data.pojo.general.TvShowsList
import com.example.tvshowreminder.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvShowRepository @Inject constructor(private val database: DatabaseContract) {

    private var page = 1

    private val popularResult = MediatorLiveData<Resource<List<TvShow>>>()
    private val latestResult = MediatorLiveData<Resource<List<TvShow>>>()
    private val favouriteResult = MediatorLiveData<Resource<List<TvShow>>>()
    private val searchResult = MediatorLiveData<Resource<List<TvShow>>>()
    private val searchFavouriteResult = MediatorLiveData<Resource<List<TvShow>>>()
    private val detailsResult = MediatorLiveData<Resource<TvShowDetails>>()
    private val seasonsResult = MediatorLiveData<Resource<SeasonDetails>>()

    fun getPopularTvShowList(language: String, isRequiredToLoad: Boolean): LiveData<Resource<List<TvShow>>> {
        if (!isRequiredToLoad)  return popularResult
        popularResult.value = Resource.create()
        page = 1
        MovieDbApiService.tvShowService().getPopularTvShowList(language = language, page = page.toString())
            .enqueue(object : Callback<TvShowsList> {
                override fun onFailure(call: Call<TvShowsList>, t: Throwable) {
                    loadPopularFromDb(popularResult, true)
                }

                override fun onResponse(call: Call<TvShowsList>, response: Response<TvShowsList>) {
                    if (response.isSuccessful){
                        response.body()?.showsList?.let {tvShowList ->
                            database.deletePopularTvShows()
                            database.insertPopularTvShowList(tvShowList){
                                loadPopularFromDb(popularResult, false)
                            }
                        }
                    }else {
                        loadPopularFromDb(popularResult, true)
                    }
                }
            })
        return popularResult
    }

    private fun loadPopularFromDb(
        popularResult: MediatorLiveData<Resource<List<TvShow>>>,
        isNetworkError: Boolean
    ){
        popularResult.addSource(database.getPopularTvShowList()){ tvShowList ->
            popularResult.removeSource(database.getPopularTvShowList())
            val sortedList = tvShowList.sortedByDescending { it.popularity }
            if (isNetworkError){
                popularResult.value = Resource.create(sortedList, ERROR_MESSAGE_NETWORK_PROBLEM_1)
            } else {
                popularResult.value = Resource.create(sortedList)
            }
        }
    }

    fun getLatestTvShowList(
        currentDate: String,
        language: String,
        isRequiredToLoad: Boolean
    ): LiveData<Resource<List<TvShow>>> {
        if (!isRequiredToLoad) return latestResult
        latestResult.value = Resource.create()
        page = 1
        MovieDbApiService.tvShowService().getLatestTvShowList(currentDate = currentDate, language = language, page = page.toString())
            .enqueue(object : Callback<TvShowsList> {
                override fun onFailure(call: Call<TvShowsList>, t: Throwable) {
                    loadLatestFromDb(latestResult, true)
                }

                override fun onResponse(call: Call<TvShowsList>, response: Response<TvShowsList>) {
                    if (response.isSuccessful){
                        response.body()?.showsList?.let {tvShowList ->
                            database.deleteLatestTvShows()
                            database.insertLatestTvShowList(tvShowList){
                                loadLatestFromDb(latestResult, false)
                            }
                        }
                    }else {
                        loadLatestFromDb(latestResult, true)
                    }
                }
            })
        return latestResult
    }

    private fun loadLatestFromDb(
        latestResult: MediatorLiveData<Resource<List<TvShow>>>,
        isNetworkError: Boolean
    ){
        latestResult.addSource(database.getLatestTvShowList()){ tvShowList ->
            latestResult.removeSource(database.getLatestTvShowList())
            val sortedList = tvShowList.sortedByDescending { it.firstAirDate }
            if (isNetworkError){
                latestResult.value = Resource.create(sortedList, ERROR_MESSAGE_NETWORK_PROBLEM_1)
            } else {
                latestResult.value = Resource.create(sortedList)
            }
        }
    }

    fun getFavouriteTvShowList(isRequiredToLoad: Boolean) : LiveData<Resource<List<TvShow>>>  {
        if (!isRequiredToLoad) return favouriteResult
        favouriteResult.value = Resource.create()
        favouriteResult.addSource(database.getFavouriteTvShowList()){ tvShowDetailsList ->
            favouriteResult.value = Resource.create(convertToTvShowList(tvShowDetailsList))
        }
        return favouriteResult
    }

    private fun convertToTvShowList(tvShowDetailListFromDb: List<TvShowDetails>): List<TvShow> {
        val tvShowList = mutableListOf<TvShow>()
        tvShowDetailListFromDb.forEach { tvShoeDetail ->
            tvShowList.add(
                TvShow(
                    id = tvShoeDetail.id, name = tvShoeDetail.name,
                    originalName = tvShoeDetail.originalName,
                    posterPath = tvShoeDetail.posterPath,
                    voteAverage = tvShoeDetail.voteAverage,
                    popularity = tvShoeDetail.popularity
                )
            )
        }
        return tvShowList
    }

    fun searchTvShowsList(query: String, language: String): LiveData<Resource<List<TvShow>>> {
        searchResult.value = Resource.create()
        MovieDbApiService.tvShowService().searchTvShow(query = query, language = language)
            .enqueue(object : Callback<TvShowsList> {
                override fun onFailure(call: Call<TvShowsList>, t: Throwable) {
                    searchResult.value = Resource.createError(ERROR_MESSAGE_NETWORK_PROBLEM_2)
                }

                override fun onResponse(call: Call<TvShowsList>, response: Response<TvShowsList>) {
                    if (response.isSuccessful) {
                        response.body()?.showsList?.let { tvShowList ->
                            searchResult.value = Resource.create(tvShowList)
                        }
                    }
                }

            })
        return searchResult
    }

    fun searchTvShowsListInFavourite(query: String) : LiveData<Resource<List<TvShow>>> {
        searchFavouriteResult.value = Resource.create()

        searchFavouriteResult.addSource(database.searchFavouriteTvShowsList(query)){ tvShowList ->
            searchFavouriteResult.value = Resource.create(tvShowList)
        }
        return searchFavouriteResult
    }




    fun getTvShowDetails(tvId: Int, language: String, isRequiredToLoad: Boolean): LiveData<Resource<TvShowDetails>> {
        if (!isRequiredToLoad) return detailsResult
        detailsResult.value = Resource.create()

        MovieDbApiService.tvShowService().getTvShowDetails(tvId, language)
            .enqueue(object : Callback<TvShowDetails> {
                override fun onFailure(call: Call<TvShowDetails>, t: Throwable) {

                    detailsResult.addSource(database.getTvShow(tvId)) { tvShowList ->
                        detailsResult.value = Resource.create(tvShowList)
                    }
                }

                override fun onResponse(
                    call: Call<TvShowDetails>,
                    response: Response<TvShowDetails>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            detailsResult.value = Resource.create(it)
                        }
                    }
                }

            })
        return detailsResult
    }


    fun getSeasonDetails(tvId: Int, seasonNumber: Int, language: String, isRequiredToLoad: Boolean): LiveData<Resource<SeasonDetails>> {
        if (!isRequiredToLoad) return seasonsResult
        seasonsResult.value = Resource.create()
        MovieDbApiService.tvShowService().getSeasonDetails(tvId, seasonNumber, language)
            .enqueue(object : Callback<SeasonDetails>{
                override fun onFailure(call: Call<SeasonDetails>, t: Throwable) {

                    seasonsResult.addSource(database.getFavouriteSeasonDetails(tvId, seasonNumber)){ seasonDetails ->
                        seasonsResult.value = Resource.create(seasonDetails)
                    }
                }

                override fun onResponse(
                    call: Call<SeasonDetails>,
                    response: Response<SeasonDetails>
                ) {
                    if (response.isSuccessful){
                        response.body()?.let {seasonDetails ->
                            seasonsResult.value = Resource.create(seasonDetails)
                        }
                    }
                }

            })
        return seasonsResult
    }

    fun insertTvShow(
        tvShowDetails: TvShowDetails
    ) {
        tvShowDetails.numberOfSeasons?.let {
            for (i in 1..it) {
                MovieDbApiService.tvShowService().getSeasonDetails(tvShowDetails.id, i)
                    .enqueue(object : Callback<SeasonDetails>{
                        override fun onFailure(call: Call<SeasonDetails>, t: Throwable) {
                        }

                        override fun onResponse(
                            call: Call<SeasonDetails>,
                            response: Response<SeasonDetails>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.let { seasonDetails ->
                                    database.insertFavouriteSeasonDetails(seasonDetails)
                                }
                            }
                        }

                    })
            }
        }
        database.insertTvShow(tvShowDetails)
    }

    fun deleteTvShow(tvShowDetails: TvShowDetails){
        database.deleteTvShow(tvShowDetails)
        database.deleteFavouriteSeasonDetails(tvShowDetails.id)
    }

    fun getPopularNextPage(language: String) {
        page++
        MovieDbApiService.tvShowService().getPopularTvShowList(language = language, page = page.toString())
            .enqueue(object : Callback<TvShowsList> {
                override fun onFailure(call: Call<TvShowsList>, t: Throwable) {
                    popularResult.value = Resource.createError(t.message ?: ERROR_MESSAGE)
                }

                override fun onResponse(call: Call<TvShowsList>, response: Response<TvShowsList>) {
                    if (response.isSuccessful){
                        response.body()?.showsList?.let {tvShowList ->
                            database.insertPopularTvShowList(tvShowList){}
                        }
                    } else {
                        popularResult.value = Resource.createError(response.message() ?: ERROR_MESSAGE)
                    }
                }
            })
    }

    fun getLatestNextPage(currentDate: String, language: String) {
        page++
        MovieDbApiService.tvShowService().getLatestTvShowList(currentDate = currentDate, language = language, page = page.toString())
            .enqueue(object : Callback<TvShowsList> {
                override fun onFailure(call: Call<TvShowsList>, t: Throwable) {
                    latestResult.value = Resource.createError(t.message ?: ERROR_MESSAGE)
                }

                override fun onResponse(call: Call<TvShowsList>, response: Response<TvShowsList>) {
                    if (response.isSuccessful){
                        response.body()?.showsList?.let {tvShowList ->
                            database.insertLatestTvShowList(tvShowList){}
                        }
                    } else {
                        latestResult.value = Resource.createError(response.message() ?: ERROR_MESSAGE)
                    }
                }
            })
    }
}


