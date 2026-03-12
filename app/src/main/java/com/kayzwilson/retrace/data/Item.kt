package com.kayzwilson.retrace.data

import java.util.UUID

enum class ItemType {
    LOST, FOUND
}

data class Item(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val location: String,
    val type: ItemType,
    val timestamp: Long = System.currentTimeMillis()
)
