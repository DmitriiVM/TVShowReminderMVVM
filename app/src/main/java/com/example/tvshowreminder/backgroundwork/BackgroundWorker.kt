package com.example.tvshowreminder.backgroundwork

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.data.database.DatabaseContract
import com.example.tvshowreminder.data.network.MovieDbApiService
import com.example.tvshowreminder.util.getDeviceLanguage
import javax.inject.Inject

class BackgroundWorker (context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    @Inject
    lateinit var database: DatabaseContract

    override suspend fun doWork(): Result {
        (applicationContext as TvShowApplication).appComponent.inject(this)

        val favouriteTvShowList = database.getFavouriteList()

        favouriteTvShowList.forEach { tvShow ->
            val tvShowDetails = MovieDbApiService.tvShowService().getTvShowDetails(tvShow.id, getDeviceLanguage())

            tvShowDetails.nextEpisodeToAir?.airDate?.let {
                applicationContext.setAlarm(tvShowDetails)
            }
        }
        return Result.success()
    }
}