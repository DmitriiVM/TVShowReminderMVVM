package com.example.tvshowreminder.data.network

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkDataSource @Inject constructor(): NetworkContract {

    override suspend fun getPopularTvShowList(language: String, page: String)
            = MovieDbApiService.tvShowService().getPopularTvShowList(language = language, page = page)

    override suspend fun getLatestTvShowList(currentDate: String, language: String, page: String)
            = MovieDbApiService.tvShowService().getLatestTvShowList(currentDate = currentDate, language = language, page = page)

    override suspend fun searchTvShow(query: String, language: String, page: String)
            = MovieDbApiService.tvShowService().searchTvShow(query, language, page)

    override suspend fun getTvShowDetails(tvId : Int, language: String)
            = MovieDbApiService.tvShowService().getTvShowDetails(tvId, language)

    override suspend fun getSeasonDetails(tvId : Int, season_number : Int, language: String)
            = MovieDbApiService.tvShowService().getSeasonDetails(tvId, season_number, language)
}