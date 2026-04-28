package com.lumen.alarm.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.lumen.alarm.domain.model.AccentColor
import com.lumen.alarm.domain.model.ThemeMode
import com.lumen.alarm.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>
) {
    private object Keys {
        val IS_24H = booleanPreferencesKey("is_24h_format")
        val FIRST_DAY = intPreferencesKey("first_day_of_week")
        val DEFAULT_SNOOZE_MIN = intPreferencesKey("default_snooze_minutes")
        val DEFAULT_SNOOZE_COUNT = intPreferencesKey("default_snooze_max")
        val DEFAULT_SOUND_URI = stringPreferencesKey("default_sound_uri")
        val DEFAULT_SOUND_NAME = stringPreferencesKey("default_sound_name")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val BACKUP_ENABLED = booleanPreferencesKey("backup_enabled")
        val BACKUP_ACCOUNT = stringPreferencesKey("backup_account")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val TOTAL_COINS = intPreferencesKey("total_coins")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LONGEST_STREAK = intPreferencesKey("longest_streak")
    }

    val preferences: Flow<UserPreferences> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            UserPreferences(
                is24HourFormat = prefs[Keys.IS_24H] ?: false,
                firstDayOfWeek = prefs[Keys.FIRST_DAY] ?: 1,
                defaultSnoozeMinutes = prefs[Keys.DEFAULT_SNOOZE_MIN] ?: 5,
                defaultSnoozeMaxCount = prefs[Keys.DEFAULT_SNOOZE_COUNT] ?: 3,
                defaultSoundUri = prefs[Keys.DEFAULT_SOUND_URI] ?: "",
                defaultSoundName = prefs[Keys.DEFAULT_SOUND_NAME] ?: "Default",
                themeMode = ThemeMode.entries.firstOrNull {
                    it.name == prefs[Keys.THEME_MODE]
                } ?: ThemeMode.DARK,
                accentColor = AccentColor.entries.firstOrNull {
                    it.name == prefs[Keys.ACCENT_COLOR]
                } ?: AccentColor.INDIGO,
                backupEnabled = prefs[Keys.BACKUP_ENABLED] ?: false,
                backupAccount = prefs[Keys.BACKUP_ACCOUNT] ?: "",
                isOnboardingComplete = prefs[Keys.ONBOARDING_COMPLETE] ?: false,
                isPremium = prefs[Keys.IS_PREMIUM] ?: false,
                totalCoins = prefs[Keys.TOTAL_COINS] ?: 0,
                currentStreak = prefs[Keys.CURRENT_STREAK] ?: 0,
                longestStreak = prefs[Keys.LONGEST_STREAK] ?: 0,
            )
        }

    suspend fun setOnboardingComplete() = dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = true }
    suspend fun set24HourFormat(value: Boolean) = dataStore.edit { it[Keys.IS_24H] = value }
    suspend fun setThemeMode(mode: ThemeMode) = dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    suspend fun setAccentColor(color: AccentColor) = dataStore.edit { it[Keys.ACCENT_COLOR] = color.name }
    suspend fun setDefaultSnooze(minutes: Int, maxCount: Int) = dataStore.edit {
        it[Keys.DEFAULT_SNOOZE_MIN] = minutes
        it[Keys.DEFAULT_SNOOZE_COUNT] = maxCount
    }
    suspend fun setDefaultSound(uri: String, name: String) = dataStore.edit {
        it[Keys.DEFAULT_SOUND_URI] = uri
        it[Keys.DEFAULT_SOUND_NAME] = name
    }
    suspend fun addCoins(amount: Int) = dataStore.edit {
        it[Keys.TOTAL_COINS] = (it[Keys.TOTAL_COINS] ?: 0) + amount
    }
    suspend fun updateStreak(current: Int, longest: Int) = dataStore.edit {
        it[Keys.CURRENT_STREAK] = current
        it[Keys.LONGEST_STREAK] = longest
    }
}
