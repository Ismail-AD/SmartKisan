package com.appdev.smartkisan.presentation.feature.farmer.chats

import com.appdev.smartkisan.domain.model.ChatMateData
import com.appdev.smartkisan.domain.model.UserEntity

data class ChatListUiState(
    val isLoading: Boolean = false,
    val recentMessages: List<ChatMateData> = emptyList(),
    val filteredChats: List<ChatMateData> = emptyList(),
    val chatWithList: List<UserEntity> = emptyList(),
    val currentTab: Int = 0,
    val query: String = "",
    val error: String? = null,

    )
