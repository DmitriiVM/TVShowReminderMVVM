package com.example.tvshowreminder.screen.main

import android.app.Application
import android.content.res.Resources
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.tvshowreminder.R
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.data.*
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.util.*
import kotlinx.coroutines.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: TvShowRepository
) : ViewModel() {

    private var job: Job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private val pageListConfig = PagedList.Config.Builder()
        .setPageSize(20)
        .build()

    var page = 1

    private val popularResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val latestResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val favouriteResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val searchResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val searchFavouriteResult = MediatorLiveData<Resource<PagedList<TvShow>>>()

    private val language = getDeviceLanguage()
    private val currentDate = getCurrentDate()

    fun getPopularTvShowList(isRestored: Boolean): LiveData<Resource<PagedList<TvShow>>> {
        if (isRestored) {
            if (popularResult.value == null){
                coroutineScope.launch {
                    loadPopularFromDb(false)
                }
            }
            return popularResult
        }

        popularResult.value = Resource.Loading()

        coroutineScope.launch {
            try {
                val response = repository
                    .getPopularTvShowList(language = language, page = "1")
                val tvShowList = response.showsList
                repository.deletePopularTvShows()
                tvShowList.forEach {
                    it.tvShowType = TYPE_POPULAR
                }
                repository.insertTvShowList(tvShowList)
                loadPopularFromDb(false)
            } catch (e: Exception) {
                loadPopularFromDb(true)
            }
        }
        return popularResult
    }

    private suspend fun loadPopularFromDb(isNetworkError: Boolean){
        val factory = repository.getPopularTvShowList()
        val boundaryCallback = PopularBoundaryCallback(repository, page)
        loadFromDb(popularResult, factory, boundaryCallback, isNetworkError)
    }

    fun getLatestTvShowList(
        isRestored: Boolean
    ): LiveData<Resource<PagedList<TvShow>>> {

        if (isRestored) {
            if (latestResult.value == null){
                coroutineScope.launch {
                    loadLatestFromDb(false)
                }
            }
            return latestResult
        }

        latestResult.value = Resource.Loading()

        coroutineScope.launch {
            try {
                val response = repository
                    .getLatestTvShowList(currentDate = currentDate, language = language, page = "1")
                val tvShowList = response.showsList
                repository.deleteLatestTvShows()
                tvShowList.forEach {
                    it.tvShowType = TYPE_LATEST
                }
                repository.insertTvShowList(tvShowList)
                loadLatestFromDb(false)
            } catch (e: Exception) {
                loadLatestFromDb(true)
            }
        }
        return latestResult
    }

    private suspend fun loadLatestFromDb(isNetworkError: Boolean){
        val factory = repository.getLatestTvShowList()
        val boundaryCallback = LatestBoundaryCallback(repository, page)
        loadFromDb(latestResult, factory, boundaryCallback, isNetworkError)
    }

    fun searchTvShowsList(
        query: String, isRestored: Boolean
    ): LiveData<Resource<PagedList<TvShow>>> {

        if (isRestored) {
            if (searchResult.value == null){
                coroutineScope.launch {
                    loadSearchResultFromDb(query, false)
                }
            }
            return searchResult
        }

        searchResult.value = Resource.Loading()

        coroutineScope.launch {
            try {
                val response = repository
                    .searchTvShow(query = query, language = language, page = "1")
                val tvShowList = response.showsList
                if (tvShowList.isNullOrEmpty()){
                    searchResult.postValue(Resource.Error(
                        TvShowApplication.context.getString(R.string.message_no_search_matches)
                    ))
                } else {
                    repository.deleteSearchResult()
                    tvShowList.forEach {
                        it.tvShowType = TYPE_SEARCH
                    }
                    repository.insertTvShowList(tvShowList)
                    loadSearchResultFromDb(query, false)
                }
            } catch (e: Exception) {
                searchResult.postValue(Resource.Error(
                    TvShowApplication.context.getString(R.string.error_network_problem_2)
                ))
            }
        }
        return searchResult
    }

    private suspend fun loadSearchResultFromDb(query: String, isNetworkError: Boolean) {
        val factory = repository.getSearchResult()
        val boundaryCallback = SearchBoundaryCallback(repository, query, page)
        loadFromDb(searchResult, factory, boundaryCallback, isNetworkError)
    }

    fun getFavouriteTvShowList(isRestored: Boolean) : LiveData<Resource<PagedList<TvShow>>> {
        if (isRestored && favouriteResult.value != null) return favouriteResult
        favouriteResult.value = Resource.Loading()

        val factory = repository.getFavouriteTvShowList()
        val tvShowsLiveData = LivePagedListBuilder(factory, pageListConfig)
            .build()

        favouriteResult.addSource(tvShowsLiveData){ tvShowDetailsList ->
            if (tvShowDetailsList.isNullOrEmpty()){
                favouriteResult.value = Resource.Error(
                    TvShowApplication.context.getString(R.string.message_no_tv_shows)
                )
            } else {
                favouriteResult.value = Resource.Success(tvShowDetailsList)
            }
        }
        return favouriteResult
    }

    fun searchTvShowsListInFavourite(query: String, isRestored: Boolean) : LiveData<Resource<PagedList<TvShow>>> {
        if (isRestored && searchFavouriteResult.value != null) return searchFavouriteResult
        searchFavouriteResult.value = Resource.Loading()
        val factory = repository.searchFavouriteTvShowsList(query)
        val tvShowLiveData = LivePagedListBuilder(factory, pageListConfig).build()

        searchFavouriteResult.addSource(tvShowLiveData){ tvShowList ->
            if (tvShowList.isNullOrEmpty()){
                searchFavouriteResult.value = Resource.Error(
                    TvShowApplication.context.getString(R.string.message_no_search_matches))
            } else {
                searchFavouriteResult.value = Resource.Success(tvShowList)
            }
        }
        return searchFavouriteResult
    }


    private suspend fun loadFromDb(result: MediatorLiveData<Resource<PagedList<TvShow>>>,
                                   factory: DataSource.Factory<Int, TvShow>,
                                   boundaryCallback: BoundaryCallback,
                                   isNetworkError: Boolean){
        val networkError = boundaryCallback.networkError
        val tvShowListLiveData = LivePagedListBuilder(factory, pageListConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()

        withContext(Dispatchers.Main){
            result.addSource(networkError){
                result.value = Resource.Error(it)
            }
            result.addSource(tvShowListLiveData){ tvShowList ->
                if (isNetworkError){
                    result.value = Resource.SuccessWithMessage(tvShowList,
                        TvShowApplication.context.getString(R.string.error_network_problem_1))
                } else {
                    result.value = Resource.Success(tvShowList)
                }
            }
        }
    }

    fun deleteFavouriteTvShow(tvShowId: Int) {
        coroutineScope.launch {
            repository.deleteTvShowById(tvShowId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}