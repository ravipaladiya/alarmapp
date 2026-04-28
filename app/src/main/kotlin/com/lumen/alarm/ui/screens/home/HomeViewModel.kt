package com.lumen.alarm.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumen.alarm.data.repository.AlarmRepository
import com.lumen.alarm.data.repository.PreferencesRepository
import com.lumen.alarm.domain.model.Alarm
import com.lumen.alarm.domain.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val alarms: List<Alarm> = emptyList(),
    val nextAlarm: Alarm? = null,
    val countdownText: String = "",
    val greeting: String = "",
    val dateText: String = "",
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false,
    val alarmToDelete: Alarm? = null,
    val showSmartWakeSuggestion: Boolean = false,
    val smartWakeSuggestedTime: String = "",
    val toastMessage: String? = null,
    val selectedAlarmIds: Set<Long> = emptySet(),
    val isMultiSelectMode: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val prefsRepo: PreferencesRepository,
) : ViewModel() {

    val prefs: StateFlow<UserPreferences> = prefsRepo.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadAlarms()
        updateGreeting()
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            combine(
                repository.observeAllAlarms(),
                repository.observeNextAlarm(),
            ) { alarms, next -> Pair(alarms, next) }
                .collect { (alarms, next) ->
                    _uiState.update { state ->
                        state.copy(
                            alarms = alarms,
                            nextAlarm = next,
                            countdownText = next?.let { buildCountdown(it) } ?: "",
                            isLoading = false,
                        )
                    }
                }
        }
    }

    private fun updateGreeting() {
        val hour = LocalDateTime.now().hour
        val greeting = when {
            hour < 5 -> "Good night"
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            hour < 21 -> "Good evening"
            else -> "Good night"
        }
        val dateText = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
        )
        _uiState.update { it.copy(greeting = greeting, dateText = dateText) }
    }

    private fun buildCountdown(alarm: Alarm): String {
        val now = System.currentTimeMillis()
        val diff = alarm.nextTriggerAt - now
        if (diff <= 0) return "Ringing now"
        val hours = diff / 3_600_000
        val minutes = (diff % 3_600_000) / 60_000
        return when {
            hours > 0 -> "in ${hours}h ${minutes}m"
            else -> "in ${minutes}m"
        }
    }

    fun setAlarmEnabled(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.setAlarmEnabled(id, enabled)
            val msg = if (enabled) "Alarm enabled" else "Alarm disabled"
            showToast(msg)
        }
    }

    fun requestDeleteAlarm(alarm: Alarm) {
        _uiState.update { it.copy(showDeleteDialog = true, alarmToDelete = alarm) }
    }

    fun confirmDeleteAlarm() {
        val alarm = _uiState.value.alarmToDelete ?: return
        viewModelScope.launch {
            repository.deleteAlarm(alarm.id)
            _uiState.update { it.copy(showDeleteDialog = false, alarmToDelete = null) }
            showToast("Alarm deleted")
        }
    }

    fun cancelDeleteAlarm() {
        _uiState.update { it.copy(showDeleteDialog = false, alarmToDelete = null) }
    }

    fun enterMultiSelect(alarmId: Long) {
        _uiState.update { it.copy(
            isMultiSelectMode = true,
            selectedAlarmIds = setOf(alarmId),
        )}
    }

    fun toggleAlarmSelection(alarmId: Long) {
        _uiState.update { state ->
            val selected = state.selectedAlarmIds.toMutableSet()
            if (alarmId in selected) selected.remove(alarmId) else selected.add(alarmId)
            state.copy(selectedAlarmIds = selected,
                isMultiSelectMode = selected.isNotEmpty())
        }
    }

    fun exitMultiSelect() {
        _uiState.update { it.copy(isMultiSelectMode = false, selectedAlarmIds = emptySet()) }
    }

    fun deleteSelected() {
        val ids = _uiState.value.selectedAlarmIds
        viewModelScope.launch {
            ids.forEach { repository.deleteAlarm(it) }
            exitMultiSelect()
            showToast("${ids.size} alarm${if (ids.size > 1) "s" else ""} deleted")
        }
    }

    fun disableSelected() {
        val ids = _uiState.value.selectedAlarmIds
        viewModelScope.launch {
            ids.forEach { repository.setAlarmEnabled(it, false) }
            exitMultiSelect()
        }
    }

    fun dismissSmartWakeSuggestion() {
        _uiState.update { it.copy(showSmartWakeSuggestion = false) }
    }

    fun acceptSmartWakeSuggestion(alarmId: Long, newHour: Int, newMinute: Int) {
        viewModelScope.launch {
            repository.getAlarmById(alarmId)?.let { alarm ->
                repository.updateAlarm(alarm.copy(hour = newHour, minute = newMinute))
            }
            _uiState.update { it.copy(showSmartWakeSuggestion = false) }
            showToast("Smart wake time updated")
        }
    }

    private fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
        viewModelScope.launch {
            delay(3000)
            _uiState.update { it.copy(toastMessage = null) }
        }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
