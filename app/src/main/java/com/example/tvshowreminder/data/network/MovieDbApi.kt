package com.example.tvshowreminder.data.network

import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShowsList
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.LANGUAGE_RUS
import com.example.tvshowreminder.util.SORT_BY_DATE_DESC
import com.example.tvshowreminder.util.SORT_BY_POPULARITY_DESC
import retrofit2.http.*


interface MovieDbApi {

    @GET("discover/tv")
    suspend fun getPopularTvShowList(
        @Query("sort_by") sortBy: String = SORT_BY_POPULARITY_DESC,
        @Query("language") language: String = LANGUAGE_RUS,
        @Query("page") page: String
    ): TvShowsList

    @GET("discover/tv")
    suspend fun getLatestTvShowList(
        @Query("sort_by") sortBy: String = SORT_BY_DATE_DESC,
        @Query("first_air_date.lte") currentDate: String,
        @Query("language") language: String = LANGUAGE_RUS,
        @Query("page") page: String
    ): TvShowsList

    @GET("search/tv")
    suspend fun searchTvShow(
        @Query("query") query: String,
        @Query("language") language: String  = LANGUAGE_RUS,
        @Query("page") page: String  = "1"
    ): TvShowsList

    @GET("tv/{tv_id}")
    suspend fun getTvShowDetails(
        @Path("tv_id") tv_id : Int,
        @Query("language") language: String  = LANGUAGE_RUS
    ): TvShowDetails

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getSeasonDetails(
        @Path("tv_id") tv_id : Int,
        @Path("season_number") season_number : Int,
        @Query("language") language: String  = LANGUAGE_RUS
    ): SeasonDetails
}