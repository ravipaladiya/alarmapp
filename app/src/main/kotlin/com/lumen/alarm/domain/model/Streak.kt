package com.lumen.alarm.domain.model

import java.time.LocalDate

data class StreakData(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCoins: Int = 0,
    val completedDates: List<LocalDate> = emptyList(),
    val freezesAvailable: Int = 1,
)

data class Goal(
    val id: Long = 0,
    val name: String,
    val type: GoalType,
    val targetDays: Int = 21,
    val currentDays: Int = 0,
    val coinReward: Int = 200,
    val alarmId: Long? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
)

enum class GoalType(val displayName: String, val emoji: String) {
    WORKOUT("Workout", "🔥"),
    DEEP_WORK("Deep work", "⚡"),
    MEDITATE("Meditate", "💜"),
    CUSTOM("Custom", "✨"),
}

data class InsightsData(
    val wakeUpRate: Float = 0f,
    val avgSnoozes: Float = 0f,
    val avgSleepHours: Float = 0f,
    val sleepGoalHours: Float = 7.5f,
    val bestStreak: Int = 0,
    val currentStreak: Int = 0,
    val wakeTimingByDay: List<Int> = emptyList(),    // minutes off-target per day
    val snoozeHeatmap: List<List<Int>> = emptyList(), // 7 days × weeks
)

data class WorldClock(
    val id: Long = 0,
    val cityName: String,
    val timezone: String,
    val hasAlarm: Boolean = false,
)

data class UserPreferences(
    val is24HourFormat: Boolean = false,
    val firstDayOfWeek: Int = 1, // Monday=1
    val defaultSnoozeMinutes: Int = 5,
    val defaultSnoozeMaxCount: Int = 3,
    val defaultSoundUri: String = "",
    val defaultSoundName: String = "Default",
    val themeMode: ThemeMode = ThemeMode.DARK,
    val accentColor: AccentColor = AccentColor.INDIGO,
    val backupEnabled: Boolean = false,
    val backupAccount: String = "",
    val isOnboardingComplete: Boolean = false,
    val isPremium: Boolean = false,
    val totalCoins: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
)

enum class ThemeMode { DARK, LIGHT, SYSTEM }
