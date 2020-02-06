package com.example.tvshowreminder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tvshowreminder.data.pojo.episode.Episode
import com.example.tvshowreminder.data.pojo.episode.NextEpisodeToAir
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.LatestTvShow
import com.example.tvshowreminder.data.pojo.general.PopularTvShow
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.DB_NAME


@Database(
    entities = [TvShow::class, LatestTvShow::class,
    PopularTvShow::class, TvShowDetails::class, SeasonDetails::class,
    Episode::class], version = 8, exportSchema = false
)
abstract class TvShowDatabase : RoomDatabase() {

    abstract fun tvShowDao(): TvShowDao
}