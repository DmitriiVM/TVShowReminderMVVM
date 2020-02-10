package com.example.tvshowreminder.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.data.pojo.general.*
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.util.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class TvShowRepository @Inject constructor(val database: DatabaseContract) {

    fun getPopularTvShowList(): DataSource.Factory<Int, TvShow> = database.getPopularTvShowList()
    fun getLatestTvShowList(): DataSource.Factory<Int, TvShow> = database.getLatestTvShowList()
    fun getSearchResult(): DataSource.Factory<Int, TvShow> = database.getSearchResult()

    suspend fun deletePopularTvShows() = database.deletePopularTvShows()
    suspend fun deleteLatestTvShows() = database.deleteLatestTvShows()
    suspend fun deleteSearchResult() = database.deleteSearchResult()

    suspend fun insertTvShowList(tvShowList: List<TvShow>) = database.insertTvShowList(tvShowList)

    fun getFavouriteTvShowList(): DataSource.Factory<Int, TvShow> = database.getFavouriteTvShowList()
    fun searchFavouriteTvShowsList(query: String): DataSource.Factory<Int, TvShow> = database.searchFavouriteTvShowsList(query)

    fun getTvShow(tvShowId: Int): LiveData<TvShowDetails> = database.getTvShow(tvShowId)
    suspend fun insertTvShow(tvShowDetails: TvShowDetails) =  database.insertTvShow(tvShowDetails)
    suspend fun deleteTvShow(tvShowDetails: TvShowDetails) = database.deleteTvShow(tvShowDetails)

    fun getFavouriteSeasonDetails(tvShowId: Int, seasonNumber: Int)
            = database.getFavouriteSeasonDetails(tvShowId, seasonNumber)
    suspend fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails) = database.insertFavouriteSeasonDetails(seasonDetails)
    suspend fun deleteFavouriteSeasonDetails(tvShowId: Int) = database.deleteFavouriteSeasonDetails(tvShowId)

    suspend fun getFavouriteList(): List<TvShow> = database.getFavouriteList()
}


