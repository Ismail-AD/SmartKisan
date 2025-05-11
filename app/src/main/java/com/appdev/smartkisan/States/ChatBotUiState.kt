package com.appdev.smartkisan.States

import android.net.Uri
import com.appdev.smartkisan.data.BotChatMessage

data class ChatBotUiState(
    val userMessage: String = "",
    val listOfMessages: MutableList<BotChatMessage> = mutableListOf(),
    val error: String? = null,
    val isLoadingInitialData: Boolean = false,  // For initial data loading
    val isSendingMessage: Boolean = false,      // For message sending operations
    val isLoadingMore: Boolean = false,
    val hasMoreMessages: Boolean = true,
    val selectedImageUris: List<Uri> = emptyList(),
    val isHistoryMode: Boolean = false,
    val currentDate: String = "",
    val availableDates: List<String> = emptyList(),
    val shouldScrollToBottom: Boolean = false,  // Flag to indicate when scrolling should occur
    val suggestedQuestions: List<String> = listOf(
        "What are common pests affecting potato crops?",
        "How to increase rice yield in rainy season?",
        "What are the best crop rotation practices for wheat farming?",
        "How to identify nutrient deficiencies in tomato plants?"
    )
)