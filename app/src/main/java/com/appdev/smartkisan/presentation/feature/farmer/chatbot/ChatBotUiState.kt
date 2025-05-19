package com.appdev.smartkisan.presentation.feature.farmer.chatbot

import android.net.Uri
import com.appdev.smartkisan.domain.model.BotChatMessage


data class ChatBotUiState(
    val showAudioPermitRationale: Boolean = false,
    val listOfMessages: MutableList<com.appdev.smartkisan.domain.model.BotChatMessage> = mutableListOf(),
    val userMessage: String = "",
    val isSendingMessage: Boolean = false,
    val isLoadingInitialData: Boolean = false,
    val isLoadingMore: Boolean = false,
    val selectedImageUris: List<Uri> = emptyList(),
    val error: String? = null,
    val suggestedQuestions: List<String> = listOf(
        "What are common pests that attack tomato plants?",
        "How to deal with leaf blight in my corn field?",
        "When is the best time to plant rice in South Asia?",
        "What are sustainable irrigation methods for dry regions?",
        "Recommend fertilizers for organic vegetable farming",
    ),
    val shouldScrollToBottom: Boolean = false,
    val hasMoreMessages: Boolean = true,
    val showRecordingDialog: Boolean = false,
    // Date-related properties
    val availableDates: List<String> = emptyList(),
    val formattedAvailableDates: Map<String, String> = emptyMap(),
    val isDateSelectorVisible: Boolean = false,
    val isDatePickerDialogVisible: Boolean = false,
    val currentDate: String = "",
    val isHistoryMode: Boolean = false,

    // Speech recognition states
    val isSpeechRecognitionActive: Boolean = false,
    val isListening: Boolean = false,
    val recognizedLanguage: String = "",
    val speechAmplitude: Float = 0f,
    val showSpeechRecognitionUI: Boolean = false,
)