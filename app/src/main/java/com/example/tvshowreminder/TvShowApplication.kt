package com.example.tvshowreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.*
import com.example.tvshowreminder.backgroundwork.BackgroundWorker
import com.example.tvshowreminder.di.AppComponent
import com.example.tvshowreminder.di.DaggerAppComponent
import com.example.tvshowreminder.util.NOTIFICATION_CHANNEL_NAME
import com.example.tvshowreminder.util.NOTIFICATION_CHANNEL_TV_SHOW
import com.example.tvshowreminder.util.REFRESH_INTERVAL_IN_DAYS
import java.util.concurrent.TimeUnit

open class TvShowApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        runWorkManager()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_TV_SHOW,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun runWorkManager(){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workerRequest = PeriodicWorkRequestBuilder<BackgroundWorker>(REFRESH_INTERVAL_IN_DAYS, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this)
            .enqueue(workerRequest)
    }
}