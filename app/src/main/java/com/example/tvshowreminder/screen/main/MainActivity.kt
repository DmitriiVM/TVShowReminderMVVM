package com.example.tvshowreminder.screen.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.tvshowreminder.R
import com.example.tvshowreminder.backgroundwork.BackgroundWorker
import com.example.tvshowreminder.util.REFRESH_INTERVAL_IN_DAYS
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.findFragmentById(R.id.fragment_container) as MainFragment? ?:
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment.newInstance())
            .commit()
    }
}



