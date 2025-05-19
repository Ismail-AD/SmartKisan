package com.appdev.smartkisan.presentation.feature.farmer.chats

sealed interface ChatListActions {
    data object GetChatWithList : ChatListActions
    data object GetMyMessages : ChatListActions
    data class MessageAUser(val receiverId: String, val name: String, val profilePic: String?) :
        ChatListActions
    data class SearchChats(val query: String) : ChatListActions
    data class UpdateQuery(val query: String) : ChatListActions
    data class CurrentSelectedTab(val selectedTab: Int) : ChatListActions
    data class DeleteChat(val receiverId: String) : ChatListActions
}