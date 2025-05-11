package com.appdev.smartkisan.States

import android.net.Uri
import com.appdev.smartkisan.data.ChatMessage

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val pendingMessages: List<ChatMessage> = emptyList(), // Add this line to track pending messages
    val messageInput: String = "",
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null,

    val receiverId: String? = null,
    val receiverName: String? = null,
    val receiverProfilePic: String? = null,

    val userName: String = "",
    val userImage: String = "",

    val selectedImageUris: List<Uri> = emptyList(),
    val selectedImagesInProgress: List<Uri> = emptyList(),

    val isChatDeleted: Boolean = false
)