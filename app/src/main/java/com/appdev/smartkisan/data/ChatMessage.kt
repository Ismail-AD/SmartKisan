package com.appdev.smartkisan.data

import com.appdev.smartkisan.Utils.MessageStatus

data class ChatMessage(
    val messageId: String? = "",
    val message: String? = "",
    val senderID: String? = "",
    val timeStamp: Long? = 0,
    val isRead: Boolean = false,
    val status: String? = MessageStatus.PENDING.name,
    val imageUrls: List<String> = emptyList()
)
