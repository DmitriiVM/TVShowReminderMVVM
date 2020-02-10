package com.example.tvshowreminder.screen.detail

import androidx.lifecycle.*
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.Resource
import com.example.tvshowreminder.util.getDeviceLanguage
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.Exception

class DetailsViewModel @Inject constructor(private val repository: TvShowRepository): ViewModel() {

    private var job: Job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private val detailsResult = MediatorLiveData<Resource<TvShowDetails>>()
    private val language = getDeviceLanguage()

    fun getTvShowDetails(tvId: Int, isRequiredToLoad: Boolean): LiveData<Resource<TvShowDetails>> {
        if (!isRequiredToLoad) return detailsResult
        detailsResult.value = Resource.create()

        coroutineScope.launch {
            try {
                val tvShowDetails =
                    MovieDbApiService.tvShowService().getTvShowDetails(tvId, language)
                withContext(Dispatchers.Main){
                    detailsResult.value = Resource.create(tvShowDetails)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    detailsResult.addSource(repository.getTvShow(tvId)) { tvShowList ->
                        tvShowList?.let {
                            detailsResult.value = Resource.create(tvShowList)
                        }
                    }
                }
            }
        }
        return detailsResult
    }

    fun insertTvShow(
        tvShowDetails: TvShowDetails
    ) {
        coroutineScope.launch {
            repository.insertTvShow(tvShowDetails)
            tvShowDetails.numberOfSeasons?.let {
                for (i in 1..it) {
                    val seasonDetails =
                        MovieDbApiService.tvShowService().getSeasonDetails(tvShowDetails.id, i)
                    repository.insertFavouriteSeasonDetails(seasonDetails)
                }
            }
        }
    }

    fun deleteTvShow(tvShowDetails: TvShowDetails){
        coroutineScope.launch {
            repository.deleteTvShow(tvShowDetails)
            repository.deleteFavouriteSeasonDetails(tvShowDetails.id)
        }
    }

    fun isPresentInList(tvShowId: Int): LiveData<Boolean> {
        val result = MediatorLiveData<Boolean>()
        result.value = false
        coroutineScope.launch {
            val tvShowList = repository.getFavouriteList()
            tvShowList.forEach {
                if (tvShowId == it.id) {
                    withContext(Dispatchers.Main){
                        result.value = true
                    }
                }
            }
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}