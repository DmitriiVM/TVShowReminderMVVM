package com.example.tvshowreminder.screen.main

import androidx.lifecycle.ViewModel
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.util.getCurrentDate
import com.example.tvshowreminder.util.getDeviceLanguage
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: TvShowRepository) : ViewModel() {

    fun getPopularTvShowList(isRequiredToLoad: Boolean) =
        repository.getPopularTvShowList(getDeviceLanguage(), isRequiredToLoad)

    fun getLatestTvShowList(isRequiredToLoad: Boolean) =
        repository.getLatestTvShowList(getCurrentDate(), getDeviceLanguage(), isRequiredToLoad)

    fun getFavouriteTvShowList(isRequiredToLoad: Boolean) =
        repository.getFavouriteTvShowList(isRequiredToLoad)

    fun searchTvShowsList(query: String, isRequiredToLoad: Boolean) =
        repository.searchTvShowsList(query, getDeviceLanguage(), isRequiredToLoad)

    fun searchTvShowsListInFavourite(query: String, isRequiredToLoad: Boolean) =
        repository.searchTvShowsListInFavourite(query, isRequiredToLoad)
}