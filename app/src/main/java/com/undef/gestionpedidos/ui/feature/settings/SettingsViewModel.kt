package com.undef.gestionpedidos.ui.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.gestionpedidos.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val notificacionesActivas: Boolean = true,
    val modoOscuroActivo: Boolean = false
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            ServiceLocator.userPreferencesRepository.isDarkMode.collect { isDark ->
                _uiState.update { it.copy(modoOscuroActivo = isDark) }
            }
        }
    }

    fun toggleNotificaciones(active: Boolean) {
        _uiState.update { it.copy(notificacionesActivas = active) }
    }

    fun toggleModoOscuro(active: Boolean) {
        viewModelScope.launch {
            ServiceLocator.userPreferencesRepository.setDarkMode(active)
        }
    }
}
