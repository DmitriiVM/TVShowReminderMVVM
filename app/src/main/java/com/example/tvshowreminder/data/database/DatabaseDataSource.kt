package com.example.tvshowreminder.data.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseDataSource @Inject constructor(
    private val tvShowDatabase: TvShowDatabase
) : DatabaseContract {

    override fun getPopularTvShowList()
            = tvShowDatabase.tvShowDao().getPopularTvShowsList()

    override fun getLatestTvShowList()
            = tvShowDatabase.tvShowDao().getLatestTvShowsList()

    override fun getSearchResult() =
        tvShowDatabase.tvShowDao().getSearchResult()

    override fun getFavouriteTvShowList()
            = tvShowDatabase.tvShowDao().getFavouriteTvShowsList()

    override suspend fun deletePopularTvShows(){
            tvShowDatabase.tvShowDao().deletePopularTvShows()
    }

    override suspend fun deleteLatestTvShows(){
            tvShowDatabase.tvShowDao().deleteLatestTvShows()
    }

    override suspend fun deleteSearchResult(){
            tvShowDatabase.tvShowDao().deleteSearchResult()
    }

    override fun getFavouriteSeasonDetails(
        tvShowId: Int,
        seasonNumber: Int
    ): LiveData<SeasonDetails> {
        val result = MediatorLiveData<SeasonDetails>()
        result.addSource(tvShowDatabase.tvShowDao().getEpisodesForSeason(tvShowId, seasonNumber)){episodesList ->
            result.addSource(tvShowDatabase.tvShowDao().getFavouriteSeasonDetails(tvShowId, seasonNumber)){ seasonsDetail ->
                seasonsDetail.episodes = episodesList
                result.value = seasonsDetail
            }
        }
        return result
    }

    override suspend fun insertTvShowList(tvShowList: List<TvShow>) {
            tvShowDatabase.tvShowDao().insertTvShowList(tvShowList)
    }

    override fun getTvShow(tvShowId: Int)  =
        tvShowDatabase.tvShowDao().getFavouriteTvShow(tvShowId)

    override suspend fun insertTvShow(tvShowDetails: TvShowDetails) {
            tvShowDatabase.tvShowDao().insertTvShow(tvShowDetails)
    }

    override suspend fun deleteTvShow(tvShowDetails: TvShowDetails) {
            tvShowDatabase.tvShowDao().deleteTvShow(tvShowDetails)
    }

    override suspend fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails) {
            val tvShowId = seasonDetails.episodes?.get(0)?.showId
            seasonDetails.showId = tvShowId
            tvShowDatabase.tvShowDao().insertFavouriteSeasonDetails(seasonDetails)
            seasonDetails.episodes?.let {
                tvShowDatabase.tvShowDao().insertEpisodes(it)
            }
    }

    override suspend fun deleteFavouriteSeasonDetails(tvShowId: Int) {
            tvShowDatabase.tvShowDao().deleteFavouriteSeasonDetail(tvShowId)
    }

    override fun searchFavouriteTvShowsList(query: String)  =
        tvShowDatabase.tvShowDao().searchTvShowsList(query)


    override suspend fun getFavouriteList() = tvShowDatabase.tvShowDao().getFavouriteList()
}