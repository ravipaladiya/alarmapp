package com.lumen.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lumen.alarm.service.AlarmRingingService

class AlarmActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, AlarmRingingService::class.java).apply {
            action = intent.action
            putExtra("alarm_id", intent.getLongExtra("alarm_id", -1))
            putExtra("snooze_minutes", intent.getIntExtra("snooze_minutes", 5))
        }
        context.startService(serviceIntent)
    }
}
