package com.appdev.smartkisan.States

import android.net.Uri
import com.appdev.smartkisan.data.Chat
import com.appdev.smartkisan.data.ChatMessage

data class ChatBotUiState(
    val userMessage: String = "",
    val suggestedQuestions: List<String> = listOf(
        "What is the best fertilizer for wheat crops?",
        "What causes yellow leaves in rice plants?",
        "How to test soil quality for better farming?",
        "What are the symptoms of late blight in potatoes?"
    ),
    val isLoading: Boolean = false,
    val selectedImageUris: List<Uri> = emptyList(),
    val error: String? = null,
    val listOfMessages: MutableList<Chat> = mutableListOf(),
)
