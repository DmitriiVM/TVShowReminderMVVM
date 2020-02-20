package com.example.tvshowreminder.data


import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.data.pojo.general.TvShowsList
import com.example.tvshowreminder.util.TYPE_LATEST

class LatestBoundaryCallback(
    repository: TvShowRepository, page: Int
) : BoundaryCallback(repository, page) {

    override val type: String = TYPE_LATEST

    override suspend fun getTvShowList(): TvShowsList = MovieDbApiService.tvShowService()
        .getLatestTvShowList(language = language, page = page.toString(), currentDate = currentDate)
}