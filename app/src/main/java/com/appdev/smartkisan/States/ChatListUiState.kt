package com.appdev.smartkisan.States

import com.appdev.smartkisan.data.ChatMateData
import com.appdev.smartkisan.data.ChatMessage
import com.appdev.smartkisan.data.UserEntity
import com.appdev.smartkisan.data.UserInfo

data class ChatListUiState(
    val isLoading: Boolean = false,
    val recentMessages: List<ChatMateData> = emptyList(),
    val filteredChats: List<ChatMateData> = emptyList(),
    val chatWithList: List<UserEntity> = emptyList(),
    val currentTab: Int = 0,
    val query: String = "",
    val error: String? = null,

)
