package com.example.tvshowreminder.data.pojo.general


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tv_shows")
data class TvShow(
    @PrimaryKey
    var id: Int,
    @SerializedName("backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String? = null,
    @SerializedName("first_air_date")
    @ColumnInfo(name = "first_air_date")
    var firstAirDate: String? = null,
    var name: String? = null,
    @SerializedName("original_name")
    @ColumnInfo(name = "original_name")
    val originalName: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    val posterPath: String? = null,
    @SerializedName("vote_average")
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double? = null,
    @SerializedName("vote_count")
    @ColumnInfo(name = "vote_count")
    val voteCount: Int? = null
)