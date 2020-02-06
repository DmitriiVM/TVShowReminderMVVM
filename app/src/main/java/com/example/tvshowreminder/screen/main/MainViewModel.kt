package com.example.tvshowreminder.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.util.Resource
import com.example.tvshowreminder.util.getCurrentDate
import com.example.tvshowreminder.util.getDeviceLanguage
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: TvShowRepository): ViewModel(){

    private val language = getDeviceLanguage()
    private val currentDate = getCurrentDate()

    fun getPopularTvShowList(isRequiredToLoad: Boolean): LiveData<Resource<PagedList<TvShow>>> =
        repository.getPopularTvShowList(language, isRequiredToLoad)

    fun getLatestTvShowList(isRequiredToLoad: Boolean) =
        repository.getLatestTvShowList(currentDate, language, isRequiredToLoad)

    fun getFavouriteTvShowList(isRequiredToLoad: Boolean) =
        repository.getFavouriteTvShowList(isRequiredToLoad)

    fun searchTvShowsList(query: String)
            = repository.searchTvShowsList(query, language)

    fun searchTvShowsListInFavourite(query: String)
            = repository.searchTvShowsListInFavourite(query)
}