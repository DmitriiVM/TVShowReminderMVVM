package com.example.tvshowreminder.data.database

import androidx.lifecycle.LiveData
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface DatabaseContract {

    fun getPopularTvShowList(): LiveData<List<TvShow>>
    fun getLatestTvShowList(): LiveData<List<TvShow>>
    fun getFavouriteTvShowList(): LiveData<List<TvShowDetails>>

    fun insertPopularTvShowList(tvShowList: List<TvShow>, successCallback: () -> Unit)
    fun insertLatestTvShowList(tvShowList: List<TvShow>, successCallback: () -> Unit)

    fun getTvShow(tvShowId: Int): LiveData<TvShowDetails>
    fun insertTvShow(tvShowDetails: TvShowDetails)
    fun deleteTvShow(tvShowDetails: TvShowDetails)

    fun getFavouriteSeasonDetails(tvShowId: Int, seasonNumber: Int): LiveData<SeasonDetails>
    fun insertFavouriteSeasonDetails(seasonDetails: SeasonDetails)
    fun deleteFavouriteSeasonDetails(tvShowId: Int)

    fun searchFavouriteTvShowsList(query: String): LiveData<List<TvShow>>
    fun deletePopularTvShows()
    fun deleteLatestTvShows()
}