package com.example.expensetracker.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("expense_tracker_prefs", Context.MODE_PRIVATE)

    private val _onboardingCompleted = MutableStateFlow(isOnboardingCompleted())
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _theme = MutableStateFlow(getThemePreference())
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    private val _pin = MutableStateFlow(getPin())
    val pin: StateFlow<String?> = _pin.asStateFlow()

    private val _pinEnabled = MutableStateFlow(isPinEnabled())
    val pinEnabled: StateFlow<Boolean> = _pinEnabled.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(isBiometricEnabled())
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean("onboarding_completed", false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean("onboarding_completed", completed).apply()
        _onboardingCompleted.value = completed
    }

    fun getThemePreference(): AppTheme {
        val themeStr = sharedPreferences.getString("app_theme", AppTheme.SYSTEM.name)
        return try {
            AppTheme.valueOf(themeStr ?: AppTheme.SYSTEM.name)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }
    }

    fun setThemePreference(theme: AppTheme) {
        sharedPreferences.edit().putString("app_theme", theme.name).apply()
        _theme.value = theme
    }

    fun getPin(): String? {
        return sharedPreferences.getString("app_pin", null)
    }

    fun setPin(pin: String?) {
        sharedPreferences.edit().putString("app_pin", pin).apply()
        _pin.value = pin
    }

    fun isPinEnabled(): Boolean {
        return sharedPreferences.getBoolean("pin_enabled", false)
    }

    fun setPinEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("pin_enabled", enabled).apply()
        _pinEnabled.value = enabled
    }

    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean("biometric_enabled", false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("biometric_enabled", enabled).apply()
        _biometricEnabled.value = enabled
    }
}
