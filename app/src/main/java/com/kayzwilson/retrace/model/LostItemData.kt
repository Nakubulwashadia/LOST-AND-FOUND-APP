package com.kayzwilson.retrace.model

data class LostItemData(
    val id: String = "",
    val itemName: String = "",
    val category: String = "",
    val college: String = "",
    val location: String = "",
    val timeLost: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val reportedBy: String = "",
    val timestamp: Long = 0L,
    val status: String = "lost"
)