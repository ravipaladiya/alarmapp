package com.lumen.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lumen.alarm.service.AlarmRingingService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "com.lumen.alarm.ACTION_ALARM_FIRE") return

        val serviceIntent = Intent(context, AlarmRingingService::class.java).apply {
            putExtra("alarm_id", intent.getLongExtra("alarm_id", -1))
            putExtra("alarm_label", intent.getStringExtra("alarm_label") ?: "")
            putExtra("alarm_sound_uri", intent.getStringExtra("alarm_sound_uri") ?: "")
            putExtra("alarm_vibration", intent.getStringExtra("alarm_vibration") ?: "STANDARD")
            putExtra("alarm_challenge", intent.getStringExtra("alarm_challenge") ?: "NONE")
            putExtra("alarm_volume_ramp", intent.getBooleanExtra("alarm_volume_ramp", true))
            putExtra("alarm_flashlight", intent.getBooleanExtra("alarm_flashlight", false))
            putExtra("is_snooze", intent.getBooleanExtra("is_snooze", false))
        }
        context.startForegroundService(serviceIntent)
    }
}
