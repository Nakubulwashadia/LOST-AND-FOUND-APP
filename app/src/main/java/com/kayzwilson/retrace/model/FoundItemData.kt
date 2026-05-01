package com.kayzwilson.retrace.model



data class FoundItemData(
    val id: String = "",
    val itemName: String = "",
    val category: String = "",
    val college: String = "",
    val location: String = "",
    val timeFound: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val reportedBy: String = "",
    val timestamp: Long = 0L,
    val status: String = "found"
)