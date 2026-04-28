package com.lumen.alarm.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumen.alarm.data.repository.PreferencesRepository
import com.lumen.alarm.domain.model.AccentColor
import com.lumen.alarm.domain.model.ThemeMode
import com.lumen.alarm.domain.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepo: PreferencesRepository,
) : ViewModel() {

    val prefs: StateFlow<UserPreferences> = prefsRepo.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    fun set24HourFormat(value: Boolean) = viewModelScope.launch { prefsRepo.set24HourFormat(value) }
    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { prefsRepo.setThemeMode(mode) }
    fun setAccentColor(color: AccentColor) = viewModelScope.launch { prefsRepo.setAccentColor(color) }
    fun setBackup(enabled: Boolean) {
        // TODO: connect to backup/sync service
    }
}
