package com.appdev.smartkisan.data

import android.net.Uri

data class Chat(
    val message: String,
    val role: String,
    val timeStamp: Long? = 0,
    val images: List<Uri> = emptyList()  // Added image support
)

enum class ChatRoleEnum(val value: String) {
    USER("user"),
    MODEL("model")
}
