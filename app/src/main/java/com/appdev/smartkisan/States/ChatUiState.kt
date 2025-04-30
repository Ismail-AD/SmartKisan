package com.appdev.smartkisan.States

import android.net.Uri
import com.appdev.smartkisan.data.ChatMessage

data class ChatUiState(
    val receiverId: String = "",
    val receiverName: String = "",
    val receiverProfilePic: String? = null,
    val messageInput: String = "",
    val selectedImageUris: List<Uri> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingChats: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null
)
