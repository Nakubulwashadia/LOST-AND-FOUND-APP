package com.kayzwilson.retrace.model

data class ReportUiState(
    val itemName: String = "",
    val category: String = "",
    val college: String = "",
    val location: String = "",
    val time: String = "",
    val description: String = "",
    val isSubmitting: Boolean = false,
    val submitError: String = ""
)