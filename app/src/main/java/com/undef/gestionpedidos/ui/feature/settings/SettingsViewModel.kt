package com.undef.gestionpedidos.ui.feature.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val notificacionesActivas: Boolean = true,
    val modoOscuroActivo: Boolean = false
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleNotificaciones(active: Boolean) {
        _uiState.update { it.copy(notificacionesActivas = active) }
    }

    fun toggleModoOscuro(active: Boolean) {
        _uiState.update { it.copy(modoOscuroActivo = active) }
    }
}
