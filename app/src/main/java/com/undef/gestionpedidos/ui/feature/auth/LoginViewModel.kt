package com.undef.gestionpedidos.ui.feature.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.undef.gestionpedidos.R

data class LoginUiState(
    val email: String = "",
    val contrasena: String = "",
    val error: String? = null,
    val isLoginSuccess: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val sessionManager = SessionManager(application)

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun updateContrasena(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, error = null) }
    }

    fun performLogin() {
        val email = _uiState.value.email
        val contrasena = _uiState.value.contrasena

        if (email.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(error = getApplication<Application>().getString(R.string.error_required_fields)) }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(error = getApplication<Application>().getString(R.string.error_invalid_email)) }
            return
        }

        _uiState.update { it.copy(error = null) }
        
        viewModelScope.launch {
            sessionManager.saveSession()
            _uiState.update { it.copy(isLoginSuccess = true) }
        }
    }

    fun onLoginSuccessHandled() {
        _uiState.update { it.copy(isLoginSuccess = false) }
    }
}
