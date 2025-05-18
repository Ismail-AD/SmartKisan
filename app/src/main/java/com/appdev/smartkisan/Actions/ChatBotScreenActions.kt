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
    object PermissionDeniedPermanent : ChatBotScreenActions

    data class LoadMessagesByDate(val date: String) : ChatBotScreenActions
    object LoadMoreMessages : ChatBotScreenActions
    object ReturnToToday : ChatBotScreenActions
    data object ShowDatePickerDialog : ChatBotScreenActions // New action for showing date picker dialog
    data object HideDatePickerDialog : ChatBotScreenActions // New action for hiding date picker dialog
    object ToggleDateSelector : ChatBotScreenActions
    object HideDateSelector : ChatBotScreenActions
    object FinishSpeechRecognition : ChatBotScreenActions
    object CloseRecordingDialog : ChatBotScreenActions // Add this new action to close dialog

    object StartSpeechRecognition : ChatBotScreenActions
    object StopSpeechRecognition : ChatBotScreenActions
    data class SpeechRecognitionResult(val text: String, val language: String) : ChatBotScreenActions
    data class UpdateAmplitude(val amplitude: Float) : ChatBotScreenActions
    object ToggleSpeechRecognitionUI : ChatBotScreenActions
    object DismissMicrophoneDialog : ChatBotScreenActions

}