package com.example.tvshowreminder.backgroundwork

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.data.TvShowRepository
import com.example.tvshowreminder.util.getDeviceLanguage
import javax.inject.Inject

class BackgroundWorker (context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    @Inject
    lateinit var repository: TvShowRepository

    override suspend fun doWork(): Result {
        (applicationContext as TvShowApplication).appComponent.inject(this)

        val favouriteTvShowList = repository.getFavouriteList()

        favouriteTvShowList.forEach { tvShow ->
            val tvShowDetails = repository.getTvShowDetails(tvShow.id, getDeviceLanguage())

            tvShowDetails.nextEpisodeToAir?.airDate?.let {
                applicationContext.setAlarm(tvShowDetails)
            }
        }
        return Result.success()
    }
}