package com.kayzwilson.retrace.ui.state

data class AccountUiState(
    val userName: String = "",
    val email: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String = ""
)