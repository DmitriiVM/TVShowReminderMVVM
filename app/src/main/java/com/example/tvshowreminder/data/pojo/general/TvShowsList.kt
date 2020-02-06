package com.example.tvshowreminder.data.pojo.general


import com.google.gson.annotations.SerializedName

data class TvShowsList(
    val page: Int,
    @SerializedName("results")
    val showsList: List<TvShow>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)