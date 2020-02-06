package com.example.tvshowreminder.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedPreferenceHelper {

    companion object {

        private var prefs: SharedPreferences? = null

        @Volatile
        private var instance: SharedPreferenceHelper? = null

        fun getInstance(context: Context): SharedPreferenceHelper = instance ?: synchronized(this) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
            instance ?: SharedPreferenceHelper().also {
                instance = it
            }
        }

    }

    fun getSignalInfo() =  prefs?.getBoolean("signal_in_notification", false)
}