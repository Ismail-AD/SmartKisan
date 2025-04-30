package com.appdev.smartkisan.Actions

import android.net.Uri

sealed interface ChatListActions {
    data object GetChatWithList : ChatListActions
    data object GetMyMessages : ChatListActions
    data class MessageAUser(val receiverId: String, val name: String, val profilePic: String?) :
        ChatListActions

    data class CurrentSelectedTab(val selectedTab: Int) : ChatListActions
}