package com.example.tvshowreminder.backgroundwork

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.data.database.DatabaseContract
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class BackgroundWorker (context: Context, params: WorkerParameters): Worker(context, params) {

    @Inject
    lateinit var database: DatabaseContract

    lateinit var disposable: Disposable

    override fun doWork(): Result {
        (applicationContext as TvShowApplication).appComponent.inject(this)

//        disposable = database.getFavouriteTvShowList()
//            .subscribe{favouriteList ->
//                favouriteList.forEach { favouriteShow ->
//                    MovieDbApiService.tvShowService().getTvShowDetails(favouriteShow.id)
//                        .subscribe {tvShow ->
//                            tvShow.nextEpisodeToAir?.airDate?.let {
//                                applicationContext.setAlarm(tvShow)
//                            }
//
//                        }
//                }
//             }
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        disposable.dispose()
    }
}