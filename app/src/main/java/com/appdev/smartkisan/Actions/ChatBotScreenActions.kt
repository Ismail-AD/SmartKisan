package com.appdev.smartkisan.Actions

import android.net.Uri

sealed interface ChatBotScreenActions {
    data object GoBack : ChatBotScreenActions
    data class SetMessage(val message: String) : ChatBotScreenActions
    data object OpenImagePicker : ChatBotScreenActions
    data class AddSelectedImages(val uris: List<Uri>) : ChatBotScreenActions
    data class RemoveImage(val uri: Uri) : ChatBotScreenActions
    data object ClearSelectedImages : ChatBotScreenActions
    data object ClearMessage : ChatBotScreenActions
    data class SendPrompt(val prompt: String) : ChatBotScreenActions
}