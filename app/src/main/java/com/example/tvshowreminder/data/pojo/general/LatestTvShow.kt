package com.example.tvshowreminder.data.pojo.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "latest_tv_shows_table")
class LatestTvShow(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,
    @ColumnInfo(name = "first_air_date")
    val firstAirDate: String?,
    val name: String?,
    @ColumnInfo(name = "original_name")
    val originalName: String?,
    val overview: String?,
    val popularity: Double?,
    @ColumnInfo(name = "poster_path")
    val posterPath: String?,
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double?,
    @ColumnInfo(name = "vote_count")
    val voteCount: Int?
)