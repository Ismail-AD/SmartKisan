package com.appdev.smartkisan.presentation.feature.chatmessages

import android.net.Uri

sealed interface ChatActions {
    data class SendMessage(val content: String, val myName: String, val myImage: String) :
        ChatActions

    data class UpdateMessageInput(val input: String) : ChatActions
    object LoadMessages : ChatActions
    data class SetReceiverInfo(
        val receiverId: String,
        val receiverName: String,
        val receiverProfilePic: String?
    ) : ChatActions

    data class DeleteChat(val receiverId: String?) : ChatActions

    data class AddSelectedImages(val uris: List<Uri>) : ChatActions
    data class RemoveImage(val uri: Uri) : ChatActions
    object ClearSelectedImages : ChatActions
    object GoBack : ChatActions
    data class ProcessSelectedImages(val uris: List<Uri>) : ChatActions
}