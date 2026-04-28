package com.lumen.alarm.ui.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumen.alarm.data.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InsightsUiState(
    val wakeUpRate: Float = 0.82f,
    val avgSnoozes: Float = 1.4f,
    val avgSleepHours: Float = 7.2f,
    val bestStreak: Int = 11,
    val currentStreak: Int = 4,
    val wakeTimingDays: List<Int> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val repository: AlarmRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInsights()
    }

    private fun loadInsights() {
        viewModelScope.launch {
            val insights = repository.getInsights()
            _uiState.update { state ->
                state.copy(
                    wakeUpRate = if (insights.wakeUpRate > 0) insights.wakeUpRate else 0.82f,
                    avgSnoozes = if (insights.avgSnoozes > 0) insights.avgSnoozes else 1.4f,
                    isLoading = false,
                )
            }
        }
    }
}
