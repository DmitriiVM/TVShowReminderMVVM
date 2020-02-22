package com.example.tvshowreminder.data.network

import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.data.pojo.general.TvShowsList
import com.example.tvshowreminder.data.pojo.season.SeasonDetails

interface NetworkContract {

    suspend fun getPopularTvShowList(language: String, page: String): TvShowsList

    suspend fun getLatestTvShowList(currentDate: String, language: String, page: String): TvShowsList

    suspend fun searchTvShow(query: String,language: String,page: String): TvShowsList

    suspend fun getTvShowDetails(tvId : Int, language: String): TvShowDetails

    suspend fun getSeasonDetails(tvId : Int, season_number : Int, language: String): SeasonDetails

}