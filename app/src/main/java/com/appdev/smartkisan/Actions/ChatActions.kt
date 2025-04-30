package com.appdev.smartkisan.Actions

import android.net.Uri

sealed interface ChatActions {
    data object LoadMessages : ChatActions
    data object GoBack : ChatActions
    data class SetReceiverInfo(
        val receiverId: String,
        val receiverName: String,
        val receiverProfilePic: String? = null
    ) : ChatActions

    data class RemoveImage(val uri: Uri) : ChatActions
    data object OpenImagePicker : ChatActions
    data class AddSelectedImages(val uris: List<Uri>) : ChatActions
    data class SendMessage(val content: String,val myImage:String,val myName:String) : ChatActions
    data class UpdateMessageInput(val input: String) : ChatActions
}