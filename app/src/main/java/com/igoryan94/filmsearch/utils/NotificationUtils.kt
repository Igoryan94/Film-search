package com.igoryan94.filmsearch.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.view.MainActivity
import kotlin.random.Random

object NotificationUtils {
    private const val NOTIFICATION_CHANNEL_ID = "FilmDetailsWatchFilmReminder"
    private val NOTIFICATION_CHANNEL_TITLE =
        App.instance.getString(R.string.notif_channel_reminders_title)
    private val NOTIFICATION_ID = Random.nextInt(1000000, 9999999)

    @Suppress("DEPRECATION", "ObsoleteSdkInt")
    fun sendNotification(context: Context, film: Film) {
        val dataIntent = Intent(context, MainActivity::class.java)
        dataIntent.putExtra("operation", "open_film_details")
        dataIntent.putExtra("target_film", film)

        val pendingIntent = PendingIntent.getActivity(
            context, 121, dataIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_TITLE,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

        val smallIcon = R.drawable.ic_vector_notify
        val contentTitle = context.getString(R.string.notification_film_watch_reminder_title)
        val contentText = context.getString(R.string.notification_film_watch_reminder_text)

        val notification = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(smallIcon)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setShowWhen(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .build()
        } else {
            NotificationCompat.Builder(
                context, NOTIFICATION_CHANNEL_ID
            ).setContentIntent(pendingIntent)
                .setSmallIcon(smallIcon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setShowWhen(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .build()
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}