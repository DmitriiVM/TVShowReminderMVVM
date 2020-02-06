package com.example.tvshowreminder.screen.detail.tabsfragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.util.Resource
import com.example.tvshowreminder.util.getDeviceLanguage
import javax.inject.Inject

class SeasonsViewModel @Inject constructor(private val repository: TvShowRepository): ViewModel() {

    private val language = getDeviceLanguage()

    fun getSeasonDetails(tvId: Int, seasonNumber: Int, isRequiredToLoad: Boolean): LiveData<Resource<SeasonDetails>>{
        return repository.getSeasonDetails(tvId, seasonNumber, language, isRequiredToLoad)
    }

}