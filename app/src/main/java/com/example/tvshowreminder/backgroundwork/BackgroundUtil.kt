package com.example.tvshowreminder.backgroundwork

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.screen.detail.DetailActivity
import com.example.tvshowreminder.util.*
import java.util.*

internal fun Context.setAlarm(tvShow: TvShowDetails) {

    val intent = Intent(this, NewEpisodeReceiver::class.java).apply {
        val bundle = Bundle()
        bundle.putParcelable(KEY_TV_SHOW_NOTIFICATION, tvShow)
        putExtra(NOTIFICATION_INTENT_EXTRA, bundle)
    }
    val pendingIntent = PendingIntent.getBroadcast(this, tvShow.id, intent, 0)

    val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    tvShow.nextEpisodeToAir?.airDate?.convertStringToDate()?.let {
        if (it.time > Date().time) {
            alarm.setExact(AlarmManager.RTC_WAKEUP, it.time + NOTIFICATION_DELAY, pendingIntent)
        }
    }
}

internal fun Context.cancelAlarm(id: Int) {
    val intent = Intent(this, NewEpisodeReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(this, id, intent, 0)
    val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarm.cancel(pendingIntent)
}

internal fun showNotification(context: Context, tvShow: TvShowDetails) {

    val intent = Intent(context, DetailActivity::class.java)
    intent.putExtra(INTENT_EXTRA_TV_SHOW_ID, tvShow.id)

    val taskStackBuilder = TaskStackBuilder.create(context)
    taskStackBuilder
        .addNextIntentWithParentStack(intent)
    val pendingIntent = taskStackBuilder.getPendingIntent(tvShow.id, PendingIntent.FLAG_UPDATE_CURRENT)

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_TV_SHOW)
        .setSmallIcon(R.drawable.ic_movie_black_24dp)
        .setContentTitle(context.getString(R.string.notification_content_title))
        .setContentText("${tvShow.name}. ${context.getString(R.string.notification_text_1)} " +
                "${tvShow.nextEpisodeToAir?.seasonNumber}, " +
                "${context.getString(R.string.notification_text_2)} " +
                "${tvShow.nextEpisodeToAir?.episodeNumber}.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    NotificationManagerCompat.from(context).notify(tvShow.id, notification)
}
