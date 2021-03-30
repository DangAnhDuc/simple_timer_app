package com.example.simpletimerapp.util

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.simpletimerapp.R
import com.example.simpletimerapp.view.MainActivity


class NotificationUtil {
    companion object {
        fun showFinishNotification(context: Context) {
            val notificationBuilder =
                getBasicNotificationBuilder(context)
            notificationBuilder.setContentTitle(context.getString(R.string.noti_timer_finished_title))
                .setContentIntent(createPendingIntent(context))
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                Constants.NOTIFICATION_CHANEL_ID,
                Constants.NOTIFICATION_CHANEL_NAME,
                true
            )
            notificationManager.notify(110, notificationBuilder.build())
        }

        fun createRunningNotification(context: Context): Notification {
            val notificationBuilder =
                getBasicNotificationBuilder(context)
            notificationBuilder.setContentTitle(context.getString(R.string.noti_timer_running_title))
                .setContentIntent(createPendingIntent(context))
                .setOngoing(true)
            return notificationBuilder.build()
        }

        private fun createPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        fun createAppNotificationChanel(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                Constants.NOTIFICATION_CHANEL_ID,
                Constants.NOTIFICATION_CHANEL_NAME,
                true
            )
        }

        fun hideTimerNotification(context: Context) {
            val nManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.cancel(110)
        }

        private fun getBasicNotificationBuilder(
            context: Context,
        )
                : NotificationCompat.Builder {
            return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANEL_ID)
                .setSmallIcon(R.drawable.ic_timer)
                .setAutoCancel(true)
                .setDefaults(0)
                .setVibrate(longArrayOf(500))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        }

        @TargetApi(26)
        private fun NotificationManager.createNotificationChannel(
            channelID: String,
            channelName: String,
            playSound: Boolean
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
                else NotificationManager.IMPORTANCE_LOW
                val nChannel = NotificationChannel(channelID, channelName, channelImportance)
                nChannel.enableLights(true)
                nChannel.lightColor = Color.BLUE
                nChannel.enableVibration(true)
                this.createNotificationChannel(nChannel)
            }
        }
    }
}