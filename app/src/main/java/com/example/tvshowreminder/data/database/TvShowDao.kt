package com.example.tvshowreminder.data.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.example.tvshowreminder.data.pojo.episode.Episode
import com.example.tvshowreminder.data.pojo.episode.NextEpisodeToAir
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.LatestTvShow
import com.example.tvshowreminder.data.pojo.general.PopularTvShow
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


@Dao
interface TvShowDao {

    @Query("SELECT * FROM popular_tv_shows_table ORDER BY popularity DESC")
    fun getPopularTvShowsList(): DataSource.Factory<Int, TvShow>

    @Query("SELECT * FROM latest_tv_shows_table ORDER BY first_air_date DESC")
    fun getLatestTvShowsList(): DataSource.Factory<Int, TvShow>

    @Query("SELECT * FROM tv_show_details")
    fun getFavouriteTvShowsList(): DataSource.Factory<Int, TvShow>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PopularTvShow::class)
    fun insertPopularTvShowList(tvShowList: List<TvShow>)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = LatestTvShow::class)
    fun insertLatestTvShowList(tvShowList: List<TvShow>)

    @Query("DELETE FROM popular_tv_shows_table")
    fun deletePopularTvShows()

    @Query("DELETE FROM latest_tv_shows_table")
    fun deleteLatestTvShows()

    @Query("SELECT * FROM tv_show_details WHERE original_name LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchTvShowsList(query: String): DataSource.Factory<Int, TvShow>

    @Query("SELECT * FROM tv_show_details WHERE id = :tvShowId")
    fun getFavouriteTvShow(tvShowId: Int): LiveData<TvShowDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTvShow(tvShowDetails: TvShowDetails)

    @Delete
    fun deleteTvShow(tvShowDetails: TvShowDetails)

    @Query("SELECT * FROM seasons_details WHERE show_id = :tvShowId AND season_number = :seasonNumber")
    fun getFavouriteSeasonDetails(tvShowId: Int, seasonNumber: Int): LiveData<SeasonDetails>

    @Query("SELECT * FROM episode WHERE show_id = :tvShowId AND season_number = :seasonNumber")
    fun getEpisodesForSeason(tvShowId: Int, seasonNumber: Int) : LiveData<List<Episode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisodes(episodes: List<Episode>)

    @Query("DELETE FROM seasons_details WHERE id = :tvShowId")
    fun deleteFavouriteSeasonDetail(tvShowId: Int)


}