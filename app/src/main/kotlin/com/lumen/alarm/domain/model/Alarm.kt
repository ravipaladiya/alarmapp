package com.lumen.alarm.domain.model

import java.time.DayOfWeek
import java.time.LocalTime

data class Alarm(
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val isEnabled: Boolean = true,
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val soundUri: String = "",
    val soundName: String = "Default",
    val vibrationPattern: VibrationPattern = VibrationPattern.STANDARD,
    val snoozeMinutes: Int = 5,
    val snoozeMaxCount: Int = 3,
    val alarmType: AlarmType = AlarmType.NORMAL,
    val volumeRampUp: Boolean = true,
    val flashlight: Boolean = false,
    val challengeType: ChallengeType = ChallengeType.NONE,
    val photoVerificationUri: String = "",
    val isSmartWake: Boolean = false,
    val smartWakeWindow: Int = 30,         // minutes before set time
    val goalId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val nextTriggerAt: Long = 0,
    val snoozeCount: Int = 0,
) {
    val timeLabel: String get() {
        val h = if (hour % 12 == 0) 12 else hour % 12
        val m = minute.toString().padStart(2, '0')
        val period = if (hour < 12) "AM" else "PM"
        return "$h:$m $period"
    }

    val time24h: String get() {
        val h = hour.toString().padStart(2, '0')
        val m = minute.toString().padStart(2, '0')
        return "$h:$m"
    }

    val repeatLabel: String get() = when {
        repeatDays.isEmpty() -> "Once"
        repeatDays.size == 7 -> "Every day"
        repeatDays == setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY) -> "Weekdays"
        repeatDays == setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) -> "Weekends"
        else -> repeatDays.sortedBy { it.value }
            .joinToString(", ") { it.name.take(3).lowercase().replaceFirstChar { c -> c.uppercase() } }
    }
}

enum class AlarmType(val displayName: String) {
    NORMAL("Standard"),
    SMART_WAKE("Smart Wake"),
    CHALLENGE("Challenge"),
    LOCATION("Location"),
}

enum class VibrationPattern(val displayName: String, val description: String) {
    OFF("Off", "No vibration"),
    SOFT("Soft", "─  ─  ─"),
    STANDARD("Standard", "── ── ──"),
    STRONG("Strong", "─── ─── ───"),
    HEARTBEAT("Heartbeat", "─ ── ─ ──"),
}

enum class ChallengeType(val displayName: String) {
    NONE("None"),
    MATH("Math puzzle"),
    SHAKE("Shake phone"),
    QR_SCAN("Scan QR code"),
    MEMORY("Memory game"),
    TYPE_PHRASE("Type a phrase"),
    PHOTO("Photo verification"),
}

enum class AccentColor(val displayName: String) {
    INDIGO("Indigo"),
    AURORA("Aurora"),
    ROSE("Rose"),
    GOLD("Gold"),
    LILAC("Lilac"),
}
