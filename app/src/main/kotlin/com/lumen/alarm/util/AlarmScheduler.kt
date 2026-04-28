package com.lumen.alarm.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.lumen.alarm.domain.model.Alarm
import com.lumen.alarm.receiver.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(alarm: Alarm): Long {
        val triggerAt = calculateNextTrigger(alarm)
        val intent = buildAlarmIntent(alarm)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        try {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerAt, pendingIntent),
                pendingIntent,
            )
        } catch (e: SecurityException) {
            // Fall back to inexact if exact permission not granted
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
        return triggerAt
    }

    fun cancelAlarm(alarmId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun scheduleSnooze(alarmId: Long, alarmLabel: String, delayMinutes: Int): Long {
        val triggerAt = System.currentTimeMillis() + delayMinutes * 60 * 1000L
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.lumen.alarm.ACTION_ALARM_FIRE"
            putExtra("alarm_id", alarmId)
            putExtra("alarm_label", alarmLabel)
            putExtra("is_snooze", true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId + 10000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        try {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerAt, pendingIntent),
                pendingIntent,
            )
        } catch (e: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
        return triggerAt
    }

    private fun calculateNextTrigger(alarm: Alarm): Long {
        val now = LocalDateTime.now()
        val zone = ZoneId.systemDefault()

        var candidate = now.withHour(alarm.hour).withMinute(alarm.minute).withSecond(0).withNano(0)
        if (!candidate.isAfter(now)) {
            candidate = candidate.plusDays(1)
        }

        if (alarm.repeatDays.isNotEmpty()) {
            var daysChecked = 0
            while (daysChecked < 8) {
                val dayOfWeek = candidate.dayOfWeek
                if (dayOfWeek in alarm.repeatDays) break
                candidate = candidate.plusDays(1)
                daysChecked++
            }
        }

        val smartOffset = if (alarm.isSmartWake) {
            -(alarm.smartWakeWindow.toLong() * 60 * 1000)
        } else 0L

        return candidate.atZone(zone).toInstant().toEpochMilli() + smartOffset
    }

    private fun buildAlarmIntent(alarm: Alarm): Intent =
        Intent(context, AlarmReceiver::class.java).apply {
            action = "com.lumen.alarm.ACTION_ALARM_FIRE"
            putExtra("alarm_id", alarm.id)
            putExtra("alarm_label", alarm.label)
            putExtra("alarm_sound_uri", alarm.soundUri)
            putExtra("alarm_vibration", alarm.vibrationPattern.name)
            putExtra("alarm_challenge", alarm.challengeType.name)
            putExtra("alarm_volume_ramp", alarm.volumeRampUp)
            putExtra("alarm_flashlight", alarm.flashlight)
            putExtra("is_snooze", false)
        }
}
