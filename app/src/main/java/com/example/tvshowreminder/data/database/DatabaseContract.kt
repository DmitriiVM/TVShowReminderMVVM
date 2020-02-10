package com.example.tvshowreminder.data.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails

interface DatabaseContract {

    fun getPopularTvShowList(): DataSource.Factory<Int, TvShow>
    fun getLatestTvShowList(): DataSource.Factory<Int, TvShow>
    fun getSearchResult(): DataSource.Factory<Int, TvShow>

    suspend fun deletePopularTvShows()
    suspend fun deleteLatestTvShows()
    suspend fun deleteSearchResult()

    suspend fun insertTvShowList(tvShowList: List<TvShow>)

    fun getFavouriteTvShowList(): DataSource.Factory<Int, TvShow>
    fun searchFavouriteTvShowsList(query: String): DataSource.Factory<Int, TvShow>

    fun getTvShow(tvShowId: Int): LiveData<TvShowDetails>
    suspend fun insertTvShow(tvShowDetails: TvShowDetails)
    suspend fun deleteTvShow(tvShowDetails: TvShowDetails)

    fun getFavouriteSeasonDetails(tvShowId: Int, seasonNumber: Int): LiveData<SeasonDetails>
    suspend fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails)
    suspend fun deleteFavouriteSeasonDetails(tvShowId: Int)

    suspend fun getFavouriteList(): List<TvShow>
}