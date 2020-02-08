package com.example.tvshowreminder.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.util.TYPE_POPULAR
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowsList
import com.example.tvshowreminder.util.getDeviceLanguage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopularBoundaryCallback(
    private val database: DatabaseContract
) : PagedList.BoundaryCallback<TvShow>() {

    private var page = 1
    private var isLoading = false
    private val language = getDeviceLanguage()

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
        MovieDbApiService.tvShowService().getPopularTvShowList(language = language, page = page.toString())
            .enqueue(object : Callback<TvShowsList> {
                override fun onFailure(call: Call<TvShowsList>, t: Throwable) {
                    _networkError.value = t.message
                    isLoading = false
                }

                override fun onResponse(call: Call<TvShowsList>, response: Response<TvShowsList>) {
                    if (response.isSuccessful){
                        response.body()?.showsList?.let {tvShowList ->
                            tvShowList.forEach { it.tvShowType = TYPE_POPULAR }
                            database.insertTvShowList(tvShowList){
                                isLoading = false
                            }
                        }
                    }else {
                        isLoading = false
                        _networkError.value = response.errorBody().toString()
                    }
                }
            })
    }
}