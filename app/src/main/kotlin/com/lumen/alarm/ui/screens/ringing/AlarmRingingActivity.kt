package com.lumen.alarm.ui.screens.ringing

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lumen.alarm.service.AlarmRingingService
import com.lumen.alarm.ui.theme.LumenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // API 27+: use Activity methods (XML attrs removed in compileSdk 35)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            getSystemService(KeyguardManager::class.java)
                ?.requestDismissKeyguard(this, null)
        } else {
            // API 26 fallback (deprecated but only reachable on exactly API 26)
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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
