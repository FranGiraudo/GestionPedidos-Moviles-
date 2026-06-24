package com.undef.gestionpedidos.ui.feature.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Distribuidor Demo",
    val userEmail: String = "demo@distribuidora.com",
    val userRole: String = "Administrador",
    val isLoggedOut: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private val sessionManager = SessionManager(application)

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }

    fun onLogoutHandled() {
        _uiState.update { it.copy(isLoggedOut = false) }
    }
}
