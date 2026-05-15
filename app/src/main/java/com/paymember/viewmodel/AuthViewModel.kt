package com.paymember.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paymember.data.remote.RemoteAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(
    private val authManager: RemoteAuthManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AuthUiState(isAuthenticated = authManager.isLoggedIn())
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isRegisterMode = !_uiState.value.isRegisterMode,
            errorMessage = null
        )
    }

    fun continueAsGuest() {
        _uiState.value = _uiState.value.copy(
            email = "",
            password = "",
            isLoading = false,
            errorMessage = null,
            isAuthenticated = true
        )
    }

    fun submitEmailPassword() {
        val current = _uiState.value
        if (current.email.isBlank() || current.password.length < 6) {
            _uiState.value = current.copy(errorMessage = "Revisa email y contraseña (mínimo 6 caracteres).")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                if (_uiState.value.isRegisterMode) {
                    authManager.register(_uiState.value.email, _uiState.value.password)
                } else {
                    authManager.login(_uiState.value.email, _uiState.value.password)
                }
            }.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = ex.message ?: "No se pudo autenticar."
                )
            }
        }
    }

    fun submitGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                authManager.loginWithGoogle(idToken)
            }.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = ex.message ?: "No se pudo iniciar sesión con Google."
                )
            }
        }
    }
}
