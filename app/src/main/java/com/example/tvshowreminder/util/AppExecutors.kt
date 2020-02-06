package com.example.tvshowreminder.util

import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object AppExecutors {

    val diskIO = DiskIOThreadExecutor()
    val mainThread = MainThreadExecutor()

    class DiskIOThreadExecutor: Executor {
        private val diskIO = Executors.newSingleThreadExecutor()
        override fun execute(command: Runnable) {
            diskIO.execute(command)
        }
    }

    class MainThreadExecutor: Executor{
        private val mainThreadHandler = android.os.Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}