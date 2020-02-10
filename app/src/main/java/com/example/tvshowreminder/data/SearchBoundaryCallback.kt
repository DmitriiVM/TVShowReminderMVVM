package com.example.tvshowreminder.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.util.TYPE_SEARCH
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowsList
import com.example.tvshowreminder.util.TYPE_LATEST
import com.example.tvshowreminder.util.getDeviceLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class SearchBoundaryCallback(
    private val repository: TvShowRepository,
    private val query: String
) : PagedList.BoundaryCallback<TvShow>() {

    private var page = 1
    private var isLoading = false
    private val language = getDeviceLanguage()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _networkError = MutableLiveData<String>()
    val networkError: LiveData<String>
        get() = _networkError

    override fun onItemAtEndLoaded(itemAtEnd: TvShow) {
        super.onItemAtEndLoaded(itemAtEnd)
        requestAndSaveData()
    }

    private fun requestAndSaveData(){
        if (isLoading) return
        isLoading = true
        page++

        coroutineScope.launch {
            try {
                val response = MovieDbApiService.tvShowService()
                    .searchTvShow(query = query, language = language, page = page.toString())
                val tvShowList = response.showsList
                tvShowList.forEach {
                    it.tvShowType = TYPE_SEARCH
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
}