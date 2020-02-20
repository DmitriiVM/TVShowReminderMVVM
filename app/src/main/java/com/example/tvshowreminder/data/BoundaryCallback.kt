package com.example.tvshowreminder.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowsList
import com.example.tvshowreminder.util.getCurrentDate
import com.example.tvshowreminder.util.getDeviceLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

abstract class BoundaryCallback(
    private val repository: TvShowRepository,
    var page: Int
) : PagedList.BoundaryCallback<TvShow>() {

    protected abstract val type: String

    private var isLoading = false
    val language = getDeviceLanguage()
    val currentDate = getCurrentDate()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _networkError = MutableLiveData<String>()
    val networkError: LiveData<String>
        get() = _networkError

    override fun onItemAtEndLoaded(itemAtEnd: TvShow) {
        super.onItemAtEndLoaded(itemAtEnd)
        requestAndSaveData()
    }

    private fun requestAndSaveData() {
        if (isLoading) return
        isLoading = true
        page++
        coroutineScope.launch {
            try {
                val response = getTvShowList()
                val tvShowList = response.showsList
                tvShowList.forEach {
                    it.tvShowType = type
                }
                repository.insertTvShowList(tvShowList)
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    _networkError.value = e.localizedMessage
                }
            } finally {
                isLoading = false
            }

        }
    }

    protected abstract suspend fun getTvShowList(): TvShowsList
}