package com.lumen.alarm.ui.screens.streaks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumen.alarm.data.repository.AlarmRepository
import com.lumen.alarm.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class StreaksUiState(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCoins: Int = 0,
    val completedDates: List<LocalDate> = emptyList(),
    val freezesAvailable: Int = 1,
)

@HiltViewModel
class StreaksViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val prefsRepo: PreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreaksUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefsRepo.preferences.collect { prefs ->
                _uiState.update {
                    it.copy(
                        currentStreak = prefs.currentStreak,
                        longestStreak = prefs.longestStreak,
                        totalCoins = prefs.totalCoins,
                    )
                }
            }
        }
    }
}
