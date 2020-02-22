package com.example.tvshowreminder.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.network.NetworkContract
import com.example.tvshowreminder.data.pojo.general.*
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import javax.inject.Inject

class TvShowRepository @Inject constructor(val database: DatabaseContract, val network: NetworkContract) {

    fun getPopularTvShowList(): DataSource.Factory<Int, TvShow> = database.getPopularTvShowList()
    fun getLatestTvShowList(): DataSource.Factory<Int, TvShow> = database.getLatestTvShowList()
    fun getSearchResult(): DataSource.Factory<Int, TvShow> = database.getSearchResult()

    suspend fun deletePopularTvShows() = database.deletePopularTvShows()
    suspend fun deleteLatestTvShows() = database.deleteLatestTvShows()
    suspend fun deleteSearchResult() = database.deleteSearchResult()

    suspend fun insertTvShowList(tvShowList: List<TvShow>) = database.insertTvShowList(tvShowList)

    fun getFavouriteTvShowList(): DataSource.Factory<Int, TvShow> = database.getFavouriteTvShowList()
    fun searchFavouriteTvShowsList(query: String): DataSource.Factory<Int, TvShow>
            = database.searchFavouriteTvShowsList(query)

    fun getTvShow(tvShowId: Int): LiveData<TvShowDetails> = database.getTvShow(tvShowId)
    suspend fun insertTvShow(tvShowDetails: TvShowDetails) =  database.insertTvShow(tvShowDetails)
    suspend fun deleteTvShow(tvShowDetails: TvShowDetails) = database.deleteTvShow(tvShowDetails)
    suspend fun deleteTvShowById(tvShowId: Int) = database.deleteTvShowById(tvShowId)

    fun getFavouriteSeasonDetails(tvShowId: Int, seasonNumber: Int)
            = database.getFavouriteSeasonDetails(tvShowId, seasonNumber)
    suspend fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails)
            = database.insertFavouriteSeasonDetails(seasonDetails)
    suspend fun deleteFavouriteSeasonDetails(tvShowId: Int)
            = database.deleteFavouriteSeasonDetails(tvShowId)

    suspend fun getFavouriteList(): List<TvShow> = database.getFavouriteList()

    suspend fun getPopularTvShowList(language: String, page: String)
            = network.getPopularTvShowList(language, page)

    suspend fun getLatestTvShowList(currentDate: String, language: String, page: String)
            = network.getLatestTvShowList(currentDate, language, page)

    suspend fun searchTvShow(query: String,language: String,page: String)
            = network.searchTvShow(query, language, page)

    suspend fun getTvShowDetails(tvId : Int, language: String)
            = network.getTvShowDetails(tvId, language)

    suspend fun getSeasonDetails(tvId : Int, season_number : Int, language: String)
            = network.getSeasonDetails(tvId, season_number, language)
}


