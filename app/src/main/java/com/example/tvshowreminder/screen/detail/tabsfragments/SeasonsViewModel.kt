package com.example.tvshowreminder.screen.detail.tabsfragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.util.Resource
import com.example.tvshowreminder.util.getDeviceLanguage
import kotlinx.coroutines.*
import javax.inject.Inject

class SeasonsViewModel @Inject constructor(private val repository: TvShowRepository) : ViewModel() {

    private var job: Job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private val language = getDeviceLanguage()


    fun getSeasonDetails(tvId: Int, seasonNumber: Int): LiveData<Resource<SeasonDetails>> {
        val seasonsResult = MediatorLiveData<Resource<SeasonDetails>>()
        seasonsResult.value = Resource.create()

        coroutineScope.launch {
            try {
                val seasonDetails =
                    repository.getSeasonDetails(tvId, seasonNumber, language)
                withContext(Dispatchers.Main) {
                    seasonsResult.value = Resource.create(seasonDetails)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    seasonsResult.addSource(
                        repository.getFavouriteSeasonDetails(
                            tvId,
                            seasonNumber
                        )
                    ) { seasonDetails ->
                        seasonsResult.value = Resource.create(seasonDetails)
                    }
                }

            }
        }
        return seasonsResult
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}