package com.example.tvshowreminder.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
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

    private val popularResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val latestResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val favouriteResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val searchResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val searchFavouriteResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val detailsResult = MediatorLiveData<Resource<TvShowDetails>>()


    private val pageListConfig = PagedList.Config.Builder()
        .setPageSize(20)
        .setPrefetchDistance(10)
        .build()

    fun getPopularTvShowList(language: String, isRequiredToLoad: Boolean): LiveData<Resource<PagedList<TvShow>>> {
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
        popularResult: MediatorLiveData<Resource<PagedList<TvShow>>>,
        isNetworkError: Boolean
    ){
        val factory = database.getPopularTvShowList()
        val boundaryCallback = PopularBoundaryCallback(database)
        val networkError = boundaryCallback.networkError
        val tvShowListLiveData = LivePagedListBuilder(factory, pageListConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()

        popularResult.addSource(networkError){
            popularResult.value = Resource.createError(it)
        }
        popularResult.addSource(tvShowListLiveData){ tvShowList ->
            if (isNetworkError){
                popularResult.value = Resource.create(tvShowList, ERROR_MESSAGE_NETWORK_PROBLEM_1)
            } else {
                popularResult.value = Resource.create(tvShowList)
            }
        }
    }

    fun getLatestTvShowList(
        currentDate: String,
        language: String,
        isRequiredToLoad: Boolean
    ): LiveData<Resource<PagedList<TvShow>>> {
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
        latestResult: MediatorLiveData<Resource<PagedList<TvShow>>>,
        isNetworkError: Boolean
    ){
        val factory = database.getLatestTvShowList()
        val boundaryCallback = LatestBoundaryCallback(database)
        val networkError = boundaryCallback.networkError
        val tvShowListLiveData = LivePagedListBuilder(factory, pageListConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()

        popularResult.addSource(networkError){
            popularResult.value = Resource.createError(it)
        }
        latestResult.addSource(tvShowListLiveData){ tvShowList ->
            if (isNetworkError){
                latestResult.value = Resource.create(tvShowList, ERROR_MESSAGE_NETWORK_PROBLEM_1)
            } else {
                latestResult.value = Resource.create(tvShowList)
            }
        }
    }

    fun getFavouriteTvShowList(isRequiredToLoad: Boolean) : LiveData<Resource<PagedList<TvShow>>>  {
        if (!isRequiredToLoad) return favouriteResult
        val factory = database.getFavouriteTvShowList()
        val tvShowsLiveData = LivePagedListBuilder(factory, pageListConfig)
            .build()

        favouriteResult.value = Resource.create()
        favouriteResult.addSource(tvShowsLiveData){ tvShowDetailsList ->
            favouriteResult.value = Resource.create(tvShowDetailsList)
        }
        return favouriteResult
    }

    fun searchTvShowsList(query: String, language: String): LiveData<Resource<PagedList<TvShow>>> {
        searchResult.value = Resource.create()
//        MovieDbApiService.tvShowService().searchTvShow(query = query, language = language)
//            .enqueue(object : Callback<TvShowsList> {
//                override fun onFailure(call: Call<TvShowsList>, t: Throwable) {
//                    searchResult.value = Resource.createError(ERROR_MESSAGE_NETWORK_PROBLEM_2)
//                }
//
//                override fun onResponse(call: Call<TvShowsList>, response: Response<TvShowsList>) {
//                    if (response.isSuccessful) {
//                        response.body()?.showsList?.let { tvShowList ->
//
//                            searchResult.value = Resource.create(pagedList)
//                        }
//                    }
//                }
//
//            })
        return searchResult
    }

    fun searchTvShowsListInFavourite(query: String) : LiveData<Resource<PagedList<TvShow>>> {
        searchFavouriteResult.value = Resource.create()

        val factory = database.searchFavouriteTvShowsList(query)
        val tvShowLiveData = LivePagedListBuilder(factory, pageListConfig).build()

        searchFavouriteResult.addSource(tvShowLiveData){ tvShowList ->
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
                        tvShowList?.let {
                            detailsResult.value = Resource.create(tvShowList)
                        }
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

    fun getSeasonDetails(tvId: Int, seasonNumber: Int, language: String): LiveData<Resource<SeasonDetails>> {
        val seasonsResult = MediatorLiveData<Resource<SeasonDetails>>()
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


