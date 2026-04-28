package com.lumen.alarm.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumen.alarm.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefsRepo: PreferencesRepository,
) : ViewModel() {

    private val _step = MutableStateFlow(0)
    val step = _step.asStateFlow()

    fun nextStep() { _step.value++ }

    fun completeOnboarding() {
        viewModelScope.launch {
            prefsRepo.setOnboardingComplete()
        }
    }
}
