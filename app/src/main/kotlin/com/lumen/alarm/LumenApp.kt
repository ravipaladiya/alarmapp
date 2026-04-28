package com.lumen.alarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LumenApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        nm.createNotificationChannel(
            NotificationChannel(
                "lumen_alarm_upcoming",
                "Upcoming Alarms",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Reminders before your alarm rings" }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                "lumen_alarm_missed",
                "Missed Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Alerts when an alarm was missed" }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                "lumen_streak",
                "Streak & Rewards",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Streak milestones and coin rewards" }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                "lumen_sync",
                "Backup & Sync",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Backup and sync status updates" }
        )
    }
}
