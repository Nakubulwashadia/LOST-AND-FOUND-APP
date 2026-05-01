package com.kayzwilson.retrace.ui.components

fun categoryEmoji(category: String): String = when {
    category.contains("Computer", ignoreCase = true) ||
            category.contains("Laptop", ignoreCase = true)   -> "💻"
    category.contains("Phone", ignoreCase = true)     -> "📱"
    category.contains("Earphone", ignoreCase = true) ||
            category.contains("Headphone", ignoreCase = true) -> "🎧"
    category.contains("ID", ignoreCase = true) ||
            category.contains("Card", ignoreCase = true)      -> "🪪"
    category.contains("Bag", ignoreCase = true) ||
            category.contains("Backpack", ignoreCase = true)  -> "🎒"
    category.contains("Book", ignoreCase = true) ||
            category.contains("Notes", ignoreCase = true)     -> "📚"
    category.contains("Glass", ignoreCase = true) ||
            category.contains("Spectacle", ignoreCase = true) -> "👓"
    category.contains("Key", ignoreCase = true)       -> "🔑"
    category.contains("Wallet", ignoreCase = true)    -> "💳"
    category.contains("Cloth", ignoreCase = true)     -> "🧥"
    category.contains("Watch", ignoreCase = true) ||
            category.contains("Jewel", ignoreCase = true)     -> "⌚"
    category.contains("Stationery", ignoreCase = true)-> "🖊️"
    else -> "📦"
}