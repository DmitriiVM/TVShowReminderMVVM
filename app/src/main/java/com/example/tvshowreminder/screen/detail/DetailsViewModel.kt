package com.example.tvshowreminder.screen.detail

import androidx.lifecycle.*
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.Resource
import com.example.tvshowreminder.util.getDeviceLanguage
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DetailsViewModel @Inject constructor(private val repository: TvShowRepository): ViewModel() {

    private val language = getDeviceLanguage()

    fun getTvShowDetails(tvId: Int, isRequiredToLoad: Boolean)
            = repository.getTvShowDetails(tvId, language, isRequiredToLoad)

    fun insertTvShow(tvShowDetails: TvShowDetails) = repository.insertTvShow(tvShowDetails)

    fun deleteTvShow(tvShowDetails: TvShowDetails) = repository.deleteTvShow(tvShowDetails)

    fun isPresentInList(tvShowId: Int): LiveData<Boolean>{
        val result = MediatorLiveData<Boolean>()
        result.value = false
        result.addSource(repository.getFavouriteTvShowList(true)){resource ->
            when (resource){
                is Resource.Success -> {
                    val tvShowList = resource.data
                    tvShowList.forEach {
                        if (tvShowId == it.id){
                            result.value = true
                        }
                    }
                }
            }
        }
        return result
    }
}