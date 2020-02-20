package com.example.tvshowreminder.data

import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.data.pojo.general.TvShowsList
import com.example.tvshowreminder.util.TYPE_SEARCH

class SearchBoundaryCallback(
    repository: TvShowRepository,
    private val query: String, page: Int
) : BoundaryCallback(repository, page) {

    override val type: String = TYPE_SEARCH

    override suspend fun getTvShowList(): TvShowsList = MovieDbApiService.tvShowService()
        .searchTvShow(query = query, language = language, page = page.toString())
}