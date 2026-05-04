package com.kayzwilson.retrace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // ─── LOGIN ─────────────────────────────────────────
    fun login(email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = AuthUiState(isSuccess = true)
                } else {
                    _uiState.value = AuthUiState(
                        error = task.exception?.message ?: "Login failed"
                    )
                }
            }
    }

    // ─── SIGN UP ───────────────────────────────────────
    fun signUp(studentId: String, email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = AuthUiState(isSuccess = true)
                } else {
                    _uiState.value = AuthUiState(
                        error = task.exception?.message ?: "Signup failed"
                    )
                }
            }
    }

    // ─── RESET STATE (IMPORTANT) ───────────────────────
    fun resetState() {
        _uiState.value = AuthUiState()
    }
}