package com.appdev.smartkisan.data

import com.appdev.smartkisan.R

data class ChatMateData(
    val chatRoomId: String? = "",
    val partnerId: String? = "",
    val lastMessage: String? = "",
    val lastMessageTime: Long? = 0,
    val unreadCount: Int = 0,
    val receiverName: String? = "",
    val receiverImage: String? = "",
    val hasImageAttachment: Boolean = false // Indicates if the last message has images
)
