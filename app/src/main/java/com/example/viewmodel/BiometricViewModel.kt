package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppPreferences
import com.example.data.BiometricRepository
import com.example.data.ProtectedApp
import com.example.data.UnlockLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BiometricViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BiometricRepository

    val preferences: StateFlow<AppPreferences>
    val protectedApps: StateFlow<List<ProtectedApp>>
    val unlockLogs: StateFlow<List<UnlockLog>>

    // Session simulator states
    private val _isAppLocallyLocked = MutableStateFlow(false)
    val isAppLocallyLocked = _isAppLocallyLocked.asStateFlow()

    private val _activelySimulatingLockedApp = MutableStateFlow<ProtectedApp?>(null)
    val activelySimulatingLockedApp = _activelySimulatingLockedApp.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BiometricRepository(database.biometricDao())
        
        preferences = repository.preferencesFlow
            .filterNotNull()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AppPreferences()
            )

        protectedApps = repository.protectedAppsFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        unlockLogs = repository.unlockLogsFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Seed default database apps if empty
        viewModelScope.launch {
            repository.getProtectedApps()
            // trigger first preferences read/creation to make sure row id=1 is populated
            repository.getPreferences()
        }
    }

    // Save preferences
    fun updatePreferences(update: (AppPreferences) -> AppPreferences) {
        viewModelScope.launch {
            val current = repository.getPreferences()
            val next = update(current)
            repository.savePreferences(next)
        }
    }

    // Toggle app lock protection
    fun toggleAppProtection(app: ProtectedApp) {
        viewModelScope.launch {
            val updated = app.copy(isProtected = !app.isProtected)
            repository.saveProtectedApp(updated)
            logUnlockAttempt(
                isSuccess = true,
                method = "System Rules",
                details = "Toggled App Lock for ${app.displayName} (${if (updated.isProtected) "Protected" else "Unprotected"})"
            )
        }
    }

    // Log a biometric unlock event
    fun logUnlockAttempt(isSuccess: Boolean, method: String, details: String) {
        viewModelScope.launch {
            repository.insertUnlockLog(isSuccess, method, details)
        }
    }

    // Clear logs
    fun clearUnlockLogs() {
        viewModelScope.launch {
            repository.clearUnlockLogs()
        }
    }

    // Simulation hooks
    fun simulateLockScreenLock() {
        _isAppLocallyLocked.value = true
    }

    fun simulateLockScreenUnlock() {
        _isAppLocallyLocked.value = false
        logUnlockAttempt(true, "Personalized Lock Screen", "Simulated lock screen unlocked successfully")
    }

    fun triggerSimulatedAppLock(app: ProtectedApp?) {
        _activelySimulatingLockedApp.value = app
    }
}
