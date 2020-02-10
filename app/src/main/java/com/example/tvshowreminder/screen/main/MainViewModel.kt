package com.example.tvshowreminder.screen.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.tvshowreminder.data.LatestBoundaryCallback
import com.example.tvshowreminder.data.PopularBoundaryCallback
import com.example.tvshowreminder.data.SearchBoundaryCallback
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.util.*
import kotlinx.coroutines.*
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: TvShowRepository, private val database: DatabaseContract) : ViewModel() {


    private var page = 1

    private var job: Job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private val pageListConfig = PagedList.Config.Builder()
        .setPageSize(20)
        .build()

    private val popularResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val latestResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val favouriteResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val searchResult = MediatorLiveData<Resource<PagedList<TvShow>>>()
    private val searchFavouriteResult = MediatorLiveData<Resource<PagedList<TvShow>>>()

    private val language = getDeviceLanguage()
    private val currentDate = getCurrentDate()

    fun getPopularTvShowList(isRequiredToLoad: Boolean): LiveData<Resource<PagedList<TvShow>>> {
        if (!isRequiredToLoad)  return popularResult
        popularResult.value = Resource.create()

        coroutineScope.launch {
            try {
                val response = MovieDbApiService.tvShowService()
                    .getPopularTvShowList(language = language, page = page.toString())
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
        val boundaryCallback = PopularBoundaryCallback(repository)
        val networkError = boundaryCallback.networkError
        val tvShowListLiveData = LivePagedListBuilder(factory, pageListConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()

        withContext(Dispatchers.Main){
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
    }

    fun getLatestTvShowList(
        isRequiredToLoad: Boolean
    ): LiveData<Resource<PagedList<TvShow>>> {
        if (!isRequiredToLoad) return latestResult
        latestResult.value = Resource.create()

        coroutineScope.launch {
            try {
                val response = MovieDbApiService.tvShowService()
                    .getLatestTvShowList(currentDate = currentDate, language = language, page = page.toString())
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
        val boundaryCallback = LatestBoundaryCallback(repository)
        val networkError = boundaryCallback.networkError
        val tvShowListLiveData = LivePagedListBuilder(factory, pageListConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()

        withContext(Dispatchers.Main){
            latestResult.addSource(networkError){
                latestResult.value = Resource.createError(it)
            }
            latestResult.addSource(tvShowListLiveData){ tvShowList ->
                if (isNetworkError){
                    latestResult.value = Resource.create(tvShowList, ERROR_MESSAGE_NETWORK_PROBLEM_1)
                } else {
                    latestResult.value = Resource.create(tvShowList)
                }
            }
        }
    }

    fun searchTvShowsList(
        query: String, isRequiredToLoad: Boolean
    ): LiveData<Resource<PagedList<TvShow>>> {
        if (!isRequiredToLoad) return searchResult
        searchResult.value = Resource.create()

        coroutineScope.launch {
            try {
                val response = MovieDbApiService.tvShowService()
                    .searchTvShow(query = query, language = language, page = page.toString())
                val tvShowList = response.showsList
                repository.deleteSearchResult()
                tvShowList.forEach {
                    it.tvShowType = TYPE_SEARCH
                }
                repository.insertTvShowList(tvShowList)
                loadSearchResultFromDb(query, false)
            } catch (e: Exception) {
                loadSearchResultFromDb(query, true)
            }
        }
        return searchResult
    }

    private suspend fun loadSearchResultFromDb(query: String, isNetworkError: Boolean) {
        val factory = repository.getSearchResult()
        val boundaryCallback = SearchBoundaryCallback(repository, query)
        val networkError = boundaryCallback.networkError
        val tvShowListLiveData = LivePagedListBuilder(factory, pageListConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()

        withContext(Dispatchers.Main) {

            searchResult.addSource(networkError) {
                searchResult.value = Resource.createError(it)
            }
            searchResult.addSource(tvShowListLiveData) { tvShowList ->
                if (isNetworkError) {
                    searchResult.value =
                        Resource.create(tvShowList, ERROR_MESSAGE_NETWORK_PROBLEM_1)
                } else {
                    searchResult.value = Resource.create(tvShowList)
                }
            }
        }
    }

    fun getFavouriteTvShowList(isRequiredToLoad: Boolean) : LiveData<Resource<PagedList<TvShow>>> {
        if (!isRequiredToLoad) return favouriteResult
        favouriteResult.value = Resource.create()

        val factory = repository.getFavouriteTvShowList()
        val tvShowsLiveData = LivePagedListBuilder(factory, pageListConfig)
            .build()

        favouriteResult.addSource(tvShowsLiveData){ tvShowDetailsList ->
            favouriteResult.value = Resource.create(tvShowDetailsList)
        }
        return favouriteResult
    }

    fun searchTvShowsListInFavourite(query: String, isRequiredToLoad: Boolean) : LiveData<Resource<PagedList<TvShow>>> {
        if (!isRequiredToLoad) return searchFavouriteResult
        searchFavouriteResult.value = Resource.create()
        val factory = repository.searchFavouriteTvShowsList(query)
        val tvShowLiveData = LivePagedListBuilder(factory, pageListConfig).build()

        searchFavouriteResult.addSource(tvShowLiveData){ tvShowList ->
            searchFavouriteResult.value = Resource.create(tvShowList)
        }
        return searchFavouriteResult
    }


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}