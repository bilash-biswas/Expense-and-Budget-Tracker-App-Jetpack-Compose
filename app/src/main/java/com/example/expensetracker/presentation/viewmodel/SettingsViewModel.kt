package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.preferences.AppTheme
import com.example.expensetracker.data.local.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val theme: StateFlow<AppTheme> = preferencesManager.theme
    val onboardingCompleted: StateFlow<Boolean> = preferencesManager.onboardingCompleted
    val pin: StateFlow<String?> = preferencesManager.pin
    val pinEnabled: StateFlow<Boolean> = preferencesManager.pinEnabled
    val biometricEnabled: StateFlow<Boolean> = preferencesManager.biometricEnabled

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesManager.setThemePreference(theme)
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            preferencesManager.setOnboardingCompleted(completed)
        }
    }

    fun setPin(newPin: String?) {
        viewModelScope.launch {
            preferencesManager.setPin(newPin)
        }
    }

    fun setPinEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setPinEnabled(enabled)
            if (!enabled) {
                // Clear pin and disable biometric bypass if pin is disabled
                preferencesManager.setPin(null)
                preferencesManager.setBiometricEnabled(false)
            }
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setBiometricEnabled(enabled)
        }
    }
}
