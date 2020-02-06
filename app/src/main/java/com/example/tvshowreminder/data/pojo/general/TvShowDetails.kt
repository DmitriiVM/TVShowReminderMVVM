package com.example.tvshowreminder.data.pojo.general


import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tvshowreminder.data.pojo.episode.NextEpisodeToAir
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
@Entity(tableName = "tv_show_details")
data class TvShowDetails(
    @SerializedName("backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String? = null,
    @SerializedName("first_air_date")
    @ColumnInfo(name = "first_air_date")
    val firstAirDate: String? = null,
    val homepage: String? = null,
    @PrimaryKey
    val id: Int,
    @SerializedName("in_production")
    @ColumnInfo(name = "in_production")
    val inProduction: Boolean? = null,
    @SerializedName("last_air_date")
    @ColumnInfo(name = "last_air_date")
    val lastAirDate: String? = null,
    val name: String? = null,

    @SerializedName("next_episode_to_air")
    @Embedded
    val nextEpisodeToAir: NextEpisodeToAir? = null,

    @SerializedName("number_of_episodes")
    @ColumnInfo(name = "number_of_episodes")
    val numberOfEpisodes: Int? = null,
    @SerializedName("number_of_seasons")
    @ColumnInfo(name = "number_of_seasons")
    val numberOfSeasons: Int? = null,
    @SerializedName("original_language")
    @ColumnInfo(name = "original_language")
    val originalLanguage: String? = null,
    @SerializedName("original_name")
    @ColumnInfo(name = "original_name")
    val originalName: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    val posterPath: String? = null,
    val status: String? = null,
    val type: String? = null,
    @SerializedName("vote_average")
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double? = null,
    @SerializedName("vote_count")
    @ColumnInfo(name = "vote_count")
    val voteCount: Int? = null
) : Parcelable