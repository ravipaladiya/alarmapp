package com.lumen.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lumen.alarm.data.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: AlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
        val relevantActions = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.LOCKED_BOOT_COMPLETED",
            "android.intent.action.QUICKBOOT_POWERON",
        )
        if (intent.action !in relevantActions) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.rescheduleAll()
            } finally {
                pending.finish()
            }
        }
    }
}
