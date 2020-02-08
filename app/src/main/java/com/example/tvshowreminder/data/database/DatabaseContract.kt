package com.example.tvshowreminder.data.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface DatabaseContract {

    fun getPopularTvShowList(): DataSource.Factory<Int, TvShow>
    fun getLatestTvShowList(): DataSource.Factory<Int, TvShow>
    fun getSearchResult(): DataSource.Factory<Int, TvShow>

    fun deletePopularTvShows()
    fun deleteLatestTvShows()
    fun deleteSearchResult()

    fun insertTvShowList(tvShowList: List<TvShow>, successCallback: () -> Unit)

    fun getFavouriteTvShowList(): DataSource.Factory<Int, TvShow>
    fun searchFavouriteTvShowsList(query: String): DataSource.Factory<Int, TvShow>

    fun getTvShow(tvShowId: Int): LiveData<TvShowDetails>
    fun insertTvShow(tvShowDetails: TvShowDetails)
    fun deleteTvShow(tvShowDetails: TvShowDetails)

    fun getFavouriteSeasonDetails(tvShowId: Int, seasonNumber: Int): LiveData<SeasonDetails>
    fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails)
    fun deleteFavouriteSeasonDetails(tvShowId: Int)


}