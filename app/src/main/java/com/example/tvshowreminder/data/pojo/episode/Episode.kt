package com.example.tvshowreminder.data.pojo.episode


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.google.gson.annotations.SerializedName

@Entity(tableName = "episode")
data class Episode(
    @SerializedName("air_date")
    @ColumnInfo(name = "air_date")
    val airDate: String?,
    @SerializedName("episode_number")
    @ColumnInfo(name = "episode_number")
    val episodeNumber: Int?,
    @PrimaryKey
    val id: Int,
    val name: String?,
    val overview: String?,
    @SerializedName("production_code")
    @ColumnInfo(name = "production_code")
    val productionCode: String?,
    @ForeignKey(
        entity = SeasonDetails::class,
        parentColumns = ["seasonNumber"],
        childColumns = ["seasonNumber"],
        onDelete = CASCADE
    )
    @SerializedName("season_number")
    @ColumnInfo(name = "season_number")
    val seasonNumber: Int?,
    @SerializedName("show_id")
    @ColumnInfo(name = "show_id")
    val showId: Int?,
    @SerializedName("still_path")
    @ColumnInfo(name = "still_path")
    val stillPath: String?,
    @SerializedName("vote_average")
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    @ColumnInfo(name = "vote_count")
    val voteCount: Int?
)