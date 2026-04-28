package com.lumen.alarm.ui.screens.ringing

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.lumen.alarm.domain.model.ThemeMode
import com.lumen.alarm.service.AlarmRingingService
import com.lumen.alarm.ui.theme.LumenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show on lock screen and turn on screen
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        val alarmId = intent.getLongExtra("alarm_id", -1)
        val label = intent.getStringExtra("alarm_label") ?: ""

        setContent {
            LumenTheme(darkTheme = true) {
                AlarmRingingScreen(
                    alarmId = alarmId,
                    alarmLabel = label,
                    onDismiss = {
                        sendCommandToService("com.lumen.alarm.ACTION_DISMISS", alarmId)
                        finishAndRemoveTask()
                    },
                    onSnooze = { minutes ->
                        sendCommandToService("com.lumen.alarm.ACTION_SNOOZE", alarmId, minutes)
                        finishAndRemoveTask()
                    },
                )
            }
        }
    }

    private fun sendCommandToService(action: String, alarmId: Long, snoozeMinutes: Int = 5) {
        val intent = Intent(this, AlarmRingingService::class.java).apply {
            this.action = action
            putExtra("alarm_id", alarmId)
            putExtra("snooze_minutes", snoozeMinutes)
        }
        startService(intent)
    }
}
