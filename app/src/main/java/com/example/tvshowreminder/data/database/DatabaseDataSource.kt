package com.example.tvshowreminder.data.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.DataSource
import com.example.tvshowreminder.data.pojo.episode.Episode
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.AppExecutors
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
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

    override fun deletePopularTvShows(){
        AppExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().deletePopularTvShows()
        }
    }

    override fun deleteLatestTvShows(){
        AppExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().deleteLatestTvShows()
        }
    }

    override fun deleteSearchResult(){
        AppExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().deleteSearchResult()
        }
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

    override fun insertTvShowList(tvShowList: List<TvShow>, successCallback: () -> Unit) {
        AppExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().insertTvShowList(tvShowList).let {
                AppExecutors.mainThread.execute {
                    successCallback()
                }
            }
        }
    }

    override fun getTvShow(tvShowId: Int)  =
        tvShowDatabase.tvShowDao().getFavouriteTvShow(tvShowId)

    override fun insertTvShow(tvShowDetails: TvShowDetails) {
        AppExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().insertTvShow(tvShowDetails)
        }
    }

    override fun deleteTvShow(tvShowDetails: TvShowDetails) {
        AppExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().deleteTvShow(tvShowDetails)
        }
    }

    override fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails) {
        AppExecutors.diskIO.execute {
            val tvShowId = seasonDetails.episodes?.get(0)?.showId
            seasonDetails.showId = tvShowId
            tvShowDatabase.tvShowDao().insertFavouriteSeasonDetails(seasonDetails)
            seasonDetails.episodes?.let {
                tvShowDatabase.tvShowDao().insertEpisodes(it)
            }
        }
    }

    override fun deleteFavouriteSeasonDetails(tvShowId: Int) {
        AppExecutors.diskIO.execute {
            tvShowDatabase.tvShowDao().deleteFavouriteSeasonDetail(tvShowId)
        }
    }

    override fun searchFavouriteTvShowsList(query: String)  =
        tvShowDatabase.tvShowDao().searchTvShowsList(query)
}