package com.example.tvshowreminder.data.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.example.tvshowreminder.data.pojo.episode.Episode
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails

@Dao
interface TvShowDao {
    
    @Query("SELECT * FROM tv_shows WHERE tvShowType = 'popular' ORDER BY popularity DESC")
    fun getPopularTvShowsList(): DataSource.Factory<Int, TvShow>

    @Query("SELECT * FROM tv_shows WHERE tvShowType = 'latest' ORDER BY first_air_date DESC")
    fun getLatestTvShowsList(): DataSource.Factory<Int, TvShow>

    @Query("SELECT * FROM tv_shows WHERE tvShowType = 'search' ORDER BY popularity DESC")
    fun getSearchResult(): DataSource.Factory<Int, TvShow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShowList(tvShowList: List<TvShow>)

    @Query("DELETE FROM tv_shows WHERE tvShowType = 'popular'")
    suspend fun deletePopularTvShows()

    @Query("DELETE FROM tv_shows WHERE tvShowType = 'latest'")
    suspend fun deleteLatestTvShows()

    @Query("DELETE FROM tv_shows WHERE tvShowType = 'search'")
    suspend fun deleteSearchResult()

    @Query("SELECT * FROM tv_show_details")
    fun getFavouriteTvShowsList(): DataSource.Factory<Int, TvShow>

    @Query("SELECT * FROM tv_show_details WHERE original_name LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchTvShowsList(query: String): DataSource.Factory<Int, TvShow>

    @Query("SELECT * FROM tv_show_details WHERE id = :tvShowId")
    fun getFavouriteTvShow(tvShowId: Int): LiveData<TvShowDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShow(tvShowDetails: TvShowDetails)

    @Delete
    suspend fun deleteTvShow(tvShowDetails: TvShowDetails)

    @Query("SELECT * FROM seasons_details WHERE show_id = :tvShowId AND season_number = :seasonNumber")
    fun getFavouriteSeasonDetails(tvShowId: Int, seasonNumber: Int): LiveData<SeasonDetails>

    @Query("SELECT * FROM episode WHERE show_id = :tvShowId AND season_number = :seasonNumber")
    fun getEpisodesForSeason(tvShowId: Int, seasonNumber: Int) : LiveData<List<Episode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<Episode>)

    @Query("DELETE FROM seasons_details WHERE id = :tvShowId")
    suspend fun deleteFavouriteSeasonDetail(tvShowId: Int)

    @Query("SELECT * FROM tv_show_details")
    suspend fun getFavouriteList(): List<TvShow>
}