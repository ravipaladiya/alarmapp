package com.lumen.alarm.ui.screens.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumen.alarm.data.repository.AlarmRepository
import com.lumen.alarm.data.repository.PreferencesRepository
import com.lumen.alarm.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

data class CreateAlarmUiState(
    val hour: Int = 7,
    val minute: Int = 0,
    val label: String = "",
    val repeatDays: Set<DayOfWeek> = setOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
    ),
    val soundUri: String = "",
    val soundName: String = "Default",
    val vibrationPattern: VibrationPattern = VibrationPattern.STANDARD,
    val snoozeMinutes: Int = 5,
    val snoozeMaxCount: Int = 3,
    val alarmType: AlarmType = AlarmType.NORMAL,
    val volumeRampUp: Boolean = true,
    val flashlight: Boolean = false,
    val challengeType: ChallengeType = ChallengeType.NONE,
    val isSmartWake: Boolean = false,
    val smartWakeWindow: Int = 30,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val hasChanges: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val showRepeatSheet: Boolean = false,
    val showSnoozeSheet: Boolean = false,
    val showVibrationSheet: Boolean = false,
    val editingAlarmId: Long? = null,
)

@HiltViewModel
class CreateAlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val prefsRepo: PreferencesRepository,
    savedState: SavedStateHandle,
) : ViewModel() {

    private val editingId: Long? = savedState.get<Long>("alarmId")?.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(CreateAlarmUiState(editingAlarmId = editingId))
    val uiState: StateFlow<CreateAlarmUiState> = _uiState.asStateFlow()

    init {
        if (editingId != null) loadAlarm(editingId)
        else loadDefaults()
    }

    private fun loadAlarm(id: Long) {
        viewModelScope.launch {
            repository.observeAlarmById(id)
                .filterNotNull()
                .take(1)
                .collect { alarm ->
                    _uiState.update { state ->
                        state.copy(
                            hour = alarm.hour,
                            minute = alarm.minute,
                            label = alarm.label,
                            repeatDays = alarm.repeatDays,
                            soundUri = alarm.soundUri,
                            soundName = alarm.soundName,
                            vibrationPattern = alarm.vibrationPattern,
                            snoozeMinutes = alarm.snoozeMinutes,
                            snoozeMaxCount = alarm.snoozeMaxCount,
                            alarmType = alarm.alarmType,
                            volumeRampUp = alarm.volumeRampUp,
                            flashlight = alarm.flashlight,
                            challengeType = alarm.challengeType,
                            isSmartWake = alarm.isSmartWake,
                            smartWakeWindow = alarm.smartWakeWindow,
                            hasChanges = false,
                        )
                    }
                }
        }
    }

    private fun loadDefaults() {
        viewModelScope.launch {
            prefsRepo.preferences.take(1).collect { prefs ->
                _uiState.update { it.copy(
                    snoozeMinutes = prefs.defaultSnoozeMinutes,
                    snoozeMaxCount = prefs.defaultSnoozeMaxCount,
                    soundUri = prefs.defaultSoundUri,
                    soundName = prefs.defaultSoundName,
                )}
            }
        }
    }

    fun setTime(hour: Int, minute: Int) =
        _uiState.update { it.copy(hour = hour, minute = minute, hasChanges = true) }

    fun setLabel(label: String) =
        _uiState.update { it.copy(label = label, hasChanges = true) }

    fun toggleRepeatDay(day: DayOfWeek) {
        _uiState.update { state ->
            val days = state.repeatDays.toMutableSet()
            if (day in days) days.remove(day) else days.add(day)
            state.copy(repeatDays = days, hasChanges = true)
        }
    }

    fun setRepeatDays(days: Set<DayOfWeek>) =
        _uiState.update { it.copy(repeatDays = days, hasChanges = true) }

    fun setSound(uri: String, name: String) =
        _uiState.update { it.copy(soundUri = uri, soundName = name, hasChanges = true) }

    fun setVibration(pattern: VibrationPattern) =
        _uiState.update { it.copy(vibrationPattern = pattern, hasChanges = true) }

    fun setSnooze(minutes: Int, maxCount: Int) =
        _uiState.update { it.copy(snoozeMinutes = minutes, snoozeMaxCount = maxCount, hasChanges = true) }

    fun setAlarmType(type: AlarmType) =
        _uiState.update { it.copy(alarmType = type, hasChanges = true) }

    fun setVolumeRampUp(on: Boolean) =
        _uiState.update { it.copy(volumeRampUp = on, hasChanges = true) }

    fun setFlashlight(on: Boolean) =
        _uiState.update { it.copy(flashlight = on, hasChanges = true) }

    fun setChallengeType(type: ChallengeType) =
        _uiState.update { it.copy(challengeType = type, hasChanges = true) }

    fun setSmartWake(on: Boolean) =
        _uiState.update { it.copy(isSmartWake = on, hasChanges = true) }

    fun showRepeatSheet() = _uiState.update { it.copy(showRepeatSheet = true) }
    fun hideRepeatSheet() = _uiState.update { it.copy(showRepeatSheet = false) }
    fun showSnoozeSheet() = _uiState.update { it.copy(showSnoozeSheet = true) }
    fun hideSnoozeSheet() = _uiState.update { it.copy(showSnoozeSheet = false) }
    fun showVibrationSheet() = _uiState.update { it.copy(showVibrationSheet = true) }
    fun hideVibrationSheet() = _uiState.update { it.copy(showVibrationSheet = false) }

    fun requestDiscard() {
        if (_uiState.value.hasChanges) {
            _uiState.update { it.copy(showDiscardDialog = true) }
        }
    }

    fun confirmDiscard() = _uiState.update { it.copy(showDiscardDialog = false, isSaved = true) }
    fun cancelDiscard() = _uiState.update { it.copy(showDiscardDialog = false) }

    fun saveAlarm() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            val alarm = Alarm(
                id = editingId ?: 0,
                hour = state.hour,
                minute = state.minute,
                label = state.label,
                isEnabled = true,
                repeatDays = state.repeatDays,
                soundUri = state.soundUri,
                soundName = state.soundName,
                vibrationPattern = state.vibrationPattern,
                snoozeMinutes = state.snoozeMinutes,
                snoozeMaxCount = state.snoozeMaxCount,
                alarmType = state.alarmType,
                volumeRampUp = state.volumeRampUp,
                flashlight = state.flashlight,
                challengeType = state.challengeType,
                isSmartWake = state.isSmartWake,
                smartWakeWindow = state.smartWakeWindow,
            )
            if (editingId != null) repository.updateAlarm(alarm)
            else repository.saveAlarm(alarm)
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
