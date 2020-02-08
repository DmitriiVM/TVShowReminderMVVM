package com.example.tvshowreminder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tvshowreminder.data.pojo.episode.Episode
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails


@Database(
    entities = [TvShow::class, TvShowDetails::class, SeasonDetails::class,
    Episode::class], version = 1, exportSchema = false
)
abstract class TvShowDatabase : RoomDatabase() {

    abstract fun tvShowDao(): TvShowDao
}