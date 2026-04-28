package com.lumen.alarm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.lumen.alarm.R
import com.lumen.alarm.data.repository.AlarmRepository
import com.lumen.alarm.ui.screens.ringing.AlarmRingingActivity
import com.lumen.alarm.util.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class AlarmRingingService : Service() {

    @Inject lateinit var repository: AlarmRepository
    @Inject lateinit var scheduler: AlarmScheduler

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var vibrator: Vibrator? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var volumeRampJob: Job? = null

    private var currentAlarmId: Long = -1
    private var currentSnoozeCount: Int = 0

    companion object {
        const val CHANNEL_ID = "lumen_alarm_ringing"
        const val NOTIFICATION_ID = 1001
        const val MAX_RING_DURATION_MS = 5 * 60 * 1000L

        fun buildFullScreenIntent(context: Context, alarmId: Long, label: String): PendingIntent {
            val intent = Intent(context, AlarmRingingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                        Intent.FLAG_FROM_BACKGROUND)
                putExtra("alarm_id", alarmId)
                putExtra("alarm_label", label)
            }
            return PendingIntent.getActivity(
                context, alarmId.toInt(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return START_NOT_STICKY

        return when (intent.action) {
            "com.lumen.alarm.ACTION_SNOOZE" -> {
                val snoozeMinutes = intent.getIntExtra("snooze_minutes", 5)
                snooze(snoozeMinutes)
                START_NOT_STICKY
            }
            "com.lumen.alarm.ACTION_DISMISS" -> {
                dismiss()
                START_NOT_STICKY
            }
            else -> {
                val alarmId = intent.getLongExtra("alarm_id", -1)
                if (alarmId == -1L) {
                    stopSelf()
                    return START_NOT_STICKY
                }
                val label = intent.getStringExtra("alarm_label") ?: ""
                val soundUri = intent.getStringExtra("alarm_sound_uri") ?: ""
                val vibrationName = intent.getStringExtra("alarm_vibration") ?: "STANDARD"
                val volumeRamp = intent.getBooleanExtra("alarm_volume_ramp", true)
                val flashlight = intent.getBooleanExtra("alarm_flashlight", false)
                val isSnooze = intent.getBooleanExtra("is_snooze", false)

                currentAlarmId = alarmId

                val notification = buildRingingNotification(alarmId, label)
                startForeground(NOTIFICATION_ID, notification)

                launchRingingActivity(alarmId, label)
                startSound(soundUri, volumeRamp)
                startVibration(vibrationName)
                if (flashlight) toggleFlashlight(true)

                // Auto-dismiss after max duration
                serviceScope.launch {
                    delay(MAX_RING_DURATION_MS)
                    if (currentAlarmId == alarmId) {
                        recordMissed(alarmId)
                        stopRinging()
                    }
                }
                START_REDELIVER_INTENT
            }
        }
    }

    private fun launchRingingActivity(alarmId: Long, label: String) {
        val intent = Intent(this, AlarmRingingActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            putExtra("alarm_id", alarmId)
            putExtra("alarm_label", label)
        }
        startActivity(intent)
    }

    private fun startSound(soundUri: String, volumeRamp: Boolean) {
        stopSound()
        val uri: Uri = when {
            soundUri.isNotBlank() -> Uri.parse(soundUri)
            else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            ).build()
        audioManager.requestAudioFocus(focusRequest)
        audioFocusRequest = focusRequest

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(applicationContext, uri)
            isLooping = true
            if (volumeRamp) setVolume(0f, 0f) else setVolume(1f, 1f)
            prepare()
            start()
        }

        if (volumeRamp) {
            volumeRampJob = serviceScope.launch {
                // Ramp from 0 to 1 over 60 seconds
                val steps = 60
                repeat(steps) { step ->
                    val volume = (step + 1).toFloat() / steps
                    mediaPlayer?.setVolume(volume, volume)
                    delay(1000)
                }
            }
        }
    }

    private fun stopSound() {
        volumeRampJob?.cancel()
        audioFocusRequest?.let {
            (getSystemService(Context.AUDIO_SERVICE) as AudioManager).abandonAudioFocusRequest(it)
        }
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    private fun startVibration(patternName: String) {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern: LongArray = when (patternName) {
            "OFF" -> return
            "SOFT" -> longArrayOf(0, 200, 800, 200, 800)
            "STRONG" -> longArrayOf(0, 500, 200, 500, 200)
            "HEARTBEAT" -> longArrayOf(0, 100, 100, 300, 400)
            else -> longArrayOf(0, 300, 400, 300, 400) // STANDARD
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }

    private fun toggleFlashlight(on: Boolean) {
        try {
            val cameraManager = getSystemService(Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
            cameraManager.setTorchMode(cameraId, on)
        } catch (_: Exception) { }
    }

    fun snooze(minutes: Int) {
        currentSnoozeCount++
        serviceScope.launch(Dispatchers.IO) {
            val alarm = repository.getAlarmById(currentAlarmId) ?: return@launch
            scheduler.scheduleSnooze(alarm.id, alarm.label, minutes)
        }
        stopRinging()
    }

    fun dismiss() {
        serviceScope.launch(Dispatchers.IO) {
            repository.getAlarmById(currentAlarmId)?.let { alarm ->
                repository.recordDismiss(
                    alarmId = alarm.id,
                    alarmLabel = alarm.label,
                    scheduledTime = alarm.nextTriggerAt,
                    snoozeCount = currentSnoozeCount,
                    coinsEarned = if (currentSnoozeCount == 0) 20 else 5,
                    streakDay = 1,
                )
                // Reschedule for next occurrence if repeating
                if (alarm.repeatDays.isNotEmpty()) {
                    val next = scheduler.scheduleAlarm(alarm)
                    repository.updateAlarm(alarm.copy(nextTriggerAt = next, snoozeCount = 0))
                } else {
                    repository.setAlarmEnabled(alarm.id, false)
                }
            }
        }
        stopRinging()
    }

    private fun recordMissed(alarmId: Long) {
        serviceScope.launch(Dispatchers.IO) {
            repository.getAlarmById(alarmId)?.let { alarm ->
                repository.recordDismiss(
                    alarmId = alarm.id,
                    alarmLabel = alarm.label,
                    scheduledTime = alarm.nextTriggerAt,
                    snoozeCount = currentSnoozeCount,
                    coinsEarned = 0,
                    streakDay = 0,
                )
            }
        }
    }

    private fun stopRinging() {
        stopSound()
        stopVibration()
        toggleFlashlight(false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "lumen:alarm_wake"
        ).apply { acquire(MAX_RING_DURATION_MS + 5000) }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alarm Ringing",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Shows when an alarm is ringing"
            setBypassDnd(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    private fun buildRingingNotification(alarmId: Long, label: String): Notification {
        val fullScreenIntent = buildFullScreenIntent(this, alarmId, label)
        val snoozeIntent = Intent(this, AlarmRingingService::class.java).apply {
            action = "com.lumen.alarm.ACTION_SNOOZE"
            putExtra("alarm_id", alarmId)
            putExtra("snooze_minutes", 5)
        }
        val dismissIntent = Intent(this, AlarmRingingService::class.java).apply {
            action = "com.lumen.alarm.ACTION_DISMISS"
            putExtra("alarm_id", alarmId)
        }
        val snoozePi = PendingIntent.getService(
            this, (alarmId + 100).toInt(), snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val dismissPi = PendingIntent.getService(
            this, (alarmId + 200).toInt(), dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm_notification)
            .setContentTitle(label.ifBlank { "Alarm" })
            .setContentText("Tap to dismiss")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_snooze, "Snooze 5m", snoozePi)
            .addAction(R.drawable.ic_dismiss, "Dismiss", dismissPi)
            .build()
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        super.onDestroy()
        stopSound()
        stopVibration()
        wakeLock?.release()
        serviceScope.cancel()
    }
}
