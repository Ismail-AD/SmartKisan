package com.appdev.smartkisan.data

data class BotChatMessage(
    val id: String = "",
    val message: String = "",
    val role: String = "",  // USER or MODEL (BOT)
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = "",  // YYYY-MM-DD format for easy querying
    val imageUrls: List<String> = emptyList()  // Supabase URLs for images
)