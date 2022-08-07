package com.example.load_app.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.load_app.DetailActivity
import com.example.load_app.R
import com.example.load_app.model.DownloadDetails


private const val NOTIFICATION_ID = 0
const val NOTIFICATION_DETAILS_KEY = "downloadDetails"

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(
    downloadDetails: DownloadDetails,
    applicationContext: Context
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        .putExtra(NOTIFICATION_DETAILS_KEY, downloadDetails)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext, NOTIFICATION_ID, contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val action = NotificationCompat.Action.Builder(0, "Show Details", contentPendingIntent).build()

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_download)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.notification_message, downloadDetails.name))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(action)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

