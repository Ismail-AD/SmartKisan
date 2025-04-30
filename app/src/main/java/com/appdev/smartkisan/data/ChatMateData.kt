package com.appdev.smartkisan.data

import com.appdev.smartkisan.R

data class ChatMateData(
    val chatRoomId: String = "",
    val partnerId: String? = "",
    var lastMessage: String? = "",
    var lastMessageTime: Long? = 0,
    val unreadCount: Int = 0,
    val receiverName: String = "",
    val receiverImage: String = ""
)
