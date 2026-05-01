package com.kayzwilson.retrace.ui.state

import android.net.Uri

data class ReportUiState(
    val itemName: String = "",
    val category: String = "",
    val college: String = "",
    val location: String = "",
    val timeLost: String = "",
    val description: String = "",
    val imageUri: Uri? = null,

    val isSubmitting: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String = ""
)