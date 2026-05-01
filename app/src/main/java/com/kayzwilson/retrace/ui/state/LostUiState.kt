package com.kayzwilson.retrace.ui.state

import com.kayzwilson.retrace.model.LostItemData

data class LostUiState(
    val items: List<LostItemData> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String = ""
)