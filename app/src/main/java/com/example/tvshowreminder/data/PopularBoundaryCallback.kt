package com.example.tvshowreminder.data


import android.util.Log
import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.util.TYPE_POPULAR
import com.example.tvshowreminder.data.pojo.general.TvShowsList

class PopularBoundaryCallback(
    repository: TvShowRepository, page: Int
) : BoundaryCallback(repository, page) {

    override val type: String = TYPE_POPULAR

    override suspend fun getTvShowList(): TvShowsList {
        
        return MovieDbApiService.tvShowService()
            .getPopularTvShowList(language = language, page = page.toString())
    }
}