package com.example.tvshowreminder.backgroundwork

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.KEY_TV_SHOW_NOTIFICATION
import com.example.tvshowreminder.util.NOTIFICATION_INTENT_EXTRA

class NewEpisodeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.getBundleExtra(NOTIFICATION_INTENT_EXTRA)
        val tvShow = bundle?.getParcelable<TvShowDetails>(KEY_TV_SHOW_NOTIFICATION)

        tvShow?.let { context?.let {
            showNotification(context, tvShow)
        } }

    }
}