package com.example.tvshowreminder.data.pojo.season


import androidx.room.*
import com.example.tvshowreminder.data.pojo.episode.Episode
import com.google.gson.annotations.SerializedName

@Entity(tableName = "seasons_details")
data class SeasonDetails @JvmOverloads constructor(
    @SerializedName("air_date")
    @ColumnInfo(name = "air_date")
    val airDate: String?,
    @Ignore
    var episodes: List<Episode>? = listOf(),
    @PrimaryKey
    val id: String,
    val name: String?,
    val overview: String?,
    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    val posterPath: String?,
    @SerializedName("season_number")
    @ColumnInfo(name = "season_number")
    val seasonNumber: Int?,
    @ColumnInfo(name = "show_id")
    var showId: Int? = null
)