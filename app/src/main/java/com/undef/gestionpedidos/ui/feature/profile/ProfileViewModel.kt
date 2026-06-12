package com.undef.gestionpedidos.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Fran Giraudo",
    val userEmail: String = "fran@distribuidora.com",
    val userRole: String = "Vendedor",
    val isSyncing: Boolean = false,
    val syncStatus: String = "Ultima sinc: Hoy"
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            ServiceLocator.userPreferencesRepository.userEmail.collect { email ->
                if (email != null) {
                    _uiState.update { it.copy(userEmail = email) }
                }
            }
        }
        viewModelScope.launch {
            ServiceLocator.userPreferencesRepository.userName.collect { name ->
                if (name != null) {
                    _uiState.update { it.copy(userName = name) }
                }
            }
        }
    }

    fun updateProfile(newName: String) {
        viewModelScope.launch {
            ServiceLocator.userPreferencesRepository.setUserName(newName)
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncStatus = "Sincronizando...") }
            val success = ServiceLocator.orderRepository.syncOrdersToCloud()
            if (success) {
                _uiState.update { it.copy(isSyncing = false, syncStatus = "Sincronizado con exito") }
            } else {
                _uiState.update { it.copy(isSyncing = false, syncStatus = "Error al sincronizar") }
            }
        }
    }
}
