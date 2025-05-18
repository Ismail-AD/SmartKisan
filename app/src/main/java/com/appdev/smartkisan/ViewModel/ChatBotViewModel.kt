package com.appdev.smartkisan.ViewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.ChatBotScreenActions
import com.appdev.smartkisan.BuildConfig
import com.appdev.smartkisan.Repository.ChatBotRepository
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.States.ChatBotUiState
import com.appdev.smartkisan.data.BotChatMessage
import com.appdev.smartkisan.data.ChatRoleEnum
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.Utils.SpeechRecognitionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    val repository: Repository,
    private val botChatRepository: ChatBotRepository,
    private val appContext: Context,
    private val speechRecognitionManager: SpeechRecognitionManager
) : ViewModel() {

    var chatBotUiState by mutableStateOf(ChatBotUiState())
        private set

    // Add a separate state for temporary speech recognition text
    private var tempRecognizedText = ""

    private val genAI by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_KEY
        )
    }

    // Formatted date for today
    private val todayFormatted: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

    init {
        // Load today's messages
        loadTodayMessages()

        // Load available dates for the history selector
        loadAvailableDates()
        // Collect speech amplitude updates
        viewModelScope.launch {
            speechRecognitionManager.speechAmplitude.collect { amplitude ->
                chatBotUiState = chatBotUiState.copy(speechAmplitude = amplitude)

            }
        }
    }



    fun onAction(action: ChatBotScreenActions) {
        when (action) {
            is ChatBotScreenActions.SetMessage -> {
                chatBotUiState = chatBotUiState.copy(userMessage = action.message)
            }

            is ChatBotScreenActions.AddSelectedImages -> {
                chatBotUiState =
                    chatBotUiState.copy(selectedImageUris = chatBotUiState.selectedImageUris + action.uris)
            }

            is ChatBotScreenActions.PermissionDeniedPermanent->{
                chatBotUiState=chatBotUiState.copy(showAudioPermitRationale = true)

            }
            is ChatBotScreenActions.ClearSelectedImages -> {
                chatBotUiState = chatBotUiState.copy(selectedImageUris = emptyList())
            }

            is ChatBotScreenActions.RemoveImage -> {
                chatBotUiState =
                    chatBotUiState.copy(selectedImageUris = chatBotUiState.selectedImageUris.filter { uri -> uri != action.uri })
            }

            is ChatBotScreenActions.ToggleDateSelector -> {
                chatBotUiState = chatBotUiState.copy(isDateSelectorVisible = !chatBotUiState.isDateSelectorVisible)
            }

            is ChatBotScreenActions.HideDateSelector -> {
                chatBotUiState = chatBotUiState.copy(isDateSelectorVisible = false)
            }


            is ChatBotScreenActions.OpenImagePicker -> {
                // This is handled by the UI with the photo picker launcher
            }

            is ChatBotScreenActions.SendPrompt -> {
                if (action.prompt.isNotBlank() || chatBotUiState.selectedImageUris.isNotEmpty()) {
                    try {
                        sendMessageWithImages(action.prompt, chatBotUiState.selectedImageUris)
                    } catch (e: Exception) {
                        chatBotUiState = chatBotUiState.copy(error = e.localizedMessage, isSendingMessage = false)
                    }
                }
            }
            is ChatBotScreenActions.ShowDatePickerDialog -> {
                chatBotUiState = chatBotUiState.copy(isDatePickerDialogVisible = true)
            }

            is ChatBotScreenActions.HideDatePickerDialog -> {
                chatBotUiState = chatBotUiState.copy(isDatePickerDialogVisible = false)
            }

            is ChatBotScreenActions.LoadMessagesByDate -> {
                loadMessagesByDate(action.date)

            }

            is ChatBotScreenActions.LoadMoreMessages -> {
                if (chatBotUiState.listOfMessages.isNotEmpty()) {
                    val oldestMessage = chatBotUiState.listOfMessages.minByOrNull { it.timestamp ?: 0L }
                    oldestMessage?.timestamp?.let { oldestTimestamp ->
                        loadMoreMessages(chatBotUiState.currentDate, oldestTimestamp)
                    }
                }
            }

            is ChatBotScreenActions.ReturnToToday -> {
                loadTodayMessages()
                chatBotUiState = chatBotUiState.copy(isDatePickerDialogVisible = false)
            }


            is ChatBotScreenActions.StartSpeechRecognition -> {
                // Reset temporary recognized text but preserve existing input
                tempRecognizedText = chatBotUiState.userMessage

                chatBotUiState = chatBotUiState.copy(
                    isListening = true,
                    isSpeechRecognitionActive = true,
                    showRecordingDialog = true
                )

                startSpeechRecognition()
            }

            is ChatBotScreenActions.StopSpeechRecognition -> {
                Log.d("ChatBotViewModel", "Stopping speech recognition, temp text: $tempRecognizedText")

                // Save current temp text before stopping
                val currentText = tempRecognizedText

                // Stop recognition
                stopSpeechRecognition()

                // Apply the recognized text to the userMessage field and close the dialog
                // Only update if we have valid text
                if (currentText.isNotEmpty()) {
                    chatBotUiState = chatBotUiState.copy(
                        userMessage = currentText,
                        showRecordingDialog = false,
                        isListening = false,
                        isSpeechRecognitionActive = false
                    )
                } else {
                    chatBotUiState = chatBotUiState.copy(
                        showRecordingDialog = false,
                        isListening = false,
                        isSpeechRecognitionActive = false
                    )
                }
            }

            is ChatBotScreenActions.CloseRecordingDialog -> {
                Log.d("ChatBotViewModel", "Closing recording dialog")

                // Save current temp text before stopping
                val currentText = tempRecognizedText

                // Stop recognition
                stopSpeechRecognition()

                // If we have accumulated text, use it
                if (currentText.isNotEmpty()) {
                    chatBotUiState = chatBotUiState.copy(
                        userMessage = currentText,
                        showRecordingDialog = false,
                        isListening = false,
                        isSpeechRecognitionActive = false
                    )
                } else {
                    chatBotUiState = chatBotUiState.copy(
                        showRecordingDialog = false,
                        isListening = false,
                        isSpeechRecognitionActive = false
                    )
                }
            }

            is ChatBotScreenActions.SpeechRecognitionResult -> {
                // Store the recognized text
                Log.d("ChatBotViewModel", "Speech recognition result: ${action.text}")

                // Only update if we actually got text
                if (action.text.isNotEmpty()) {
                    tempRecognizedText = action.text

                    // Update state and ensure text is applied to input field
                    chatBotUiState = chatBotUiState.copy(
                        recognizedLanguage = action.language,
                        isListening = false,
                        isSpeechRecognitionActive = false,
                        userMessage = action.text, // Directly apply text to the input field
                        showRecordingDialog = false // Auto-close the dialog
                    )
                } else if (tempRecognizedText.isNotEmpty()) {
                    // If result is empty but we have accumulated text, use that
                    chatBotUiState = chatBotUiState.copy(
                        recognizedLanguage = action.language,
                        isListening = false,
                        isSpeechRecognitionActive = false,
                        userMessage = tempRecognizedText, // Use accumulated text
                        showRecordingDialog = false // Auto-close the dialog
                    )
                } else {
                    // Just close the dialog if no text at all
                    chatBotUiState = chatBotUiState.copy(
                        isListening = false,
                        isSpeechRecognitionActive = false,
                        showRecordingDialog = false
                    )
                }
            }

            is ChatBotScreenActions.FinishSpeechRecognition -> {
                val currentText = tempRecognizedText
                stopSpeechRecognition()

                // Apply the recognized text to the userMessage field
                if (currentText.isNotEmpty()) {
                    chatBotUiState = chatBotUiState.copy(
                        userMessage = currentText,
                        showRecordingDialog = false,
                        isListening = false,
                        isSpeechRecognitionActive = false
                    )
                } else {
                    chatBotUiState = chatBotUiState.copy(
                        showRecordingDialog = false,
                        isListening = false,
                        isSpeechRecognitionActive = false
                    )
                }
            }


            ChatBotScreenActions.DismissMicrophoneDialog->{
                chatBotUiState = chatBotUiState.copy(showAudioPermitRationale = false)
            }

            else -> {}
        }
    }


    private fun startSpeechRecognition() {
        if (chatBotUiState.isSpeechRecognitionActive) {
            Log.d("ChatBotViewModel", "Speech recognition already active, stopping first")
            stopSpeechRecognition()
        }

        // Store current text (if any) as initial text
        val initialText = chatBotUiState.userMessage.trim()

        // Reset temp recognized text but preserve existing input
        tempRecognizedText = initialText

        chatBotUiState = chatBotUiState.copy(
            isListening = true,
            isSpeechRecognitionActive = true,
            showRecordingDialog = true,
        )

        Log.d("ChatBotViewModel", "Starting speech recognition with initial text: $initialText")

        speechRecognitionManager.startListening(
            initialText = initialText, // Pass existing text to preserve it
            onResult = { text, language ->
                viewModelScope.launch {
                    Log.d("ChatBotViewModel", "Final speech result received: $text")
                    // Only update if we actually got text back
                    if (text.isNotEmpty()) {
                        tempRecognizedText = text
                        // This will close the dialog and set the text
                        onAction(ChatBotScreenActions.SpeechRecognitionResult(text, language))
                    } else if (tempRecognizedText.isNotEmpty()) {
                        // If we have accumulated partial results, use those
                        onAction(ChatBotScreenActions.SpeechRecognitionResult(tempRecognizedText, language))
                    } else {
                        // Just close the dialog, keep existing text
                        chatBotUiState = chatBotUiState.copy(
                            showRecordingDialog = false,
                            isListening = false,
                            isSpeechRecognitionActive = false
                        )
                    }
                }
            },
            onError = { errorCode ->
                viewModelScope.launch {
                    Log.d("ChatBotViewModel", "Speech recognition error: $errorCode")

                    // Even with error, we might have accumulated partial results
                    if (tempRecognizedText.isNotEmpty()) {
                        chatBotUiState = chatBotUiState.copy(
                            isListening = false,
                            isSpeechRecognitionActive = false,
                            userMessage = tempRecognizedText,
                            showRecordingDialog = false
                        )
                    } else {
                        chatBotUiState = chatBotUiState.copy(
                            isListening = false,
                            isSpeechRecognitionActive = false,
                            showRecordingDialog = false
                        )
                    }
                }
            },
            onPartialResult = { partialText ->
                viewModelScope.launch {
                    // Update the temporary variable with partial results
                    Log.d("ChatBotViewModel", "Partial speech result: $partialText")
                    if (partialText.isNotEmpty()) {
                        tempRecognizedText = partialText
                    }
                }
            }
        )
    }

    private fun stopSpeechRecognition() {
        Log.d("ChatBotViewModel", "ViewModel stopping speech recognition")

        // Pass current temp text before stopping
        val currentTempText = tempRecognizedText

        // Stop speech recognition
        speechRecognitionManager.stopListening()

        // Update UI state to show we're no longer listening
        chatBotUiState = chatBotUiState.copy(
            isListening = false,
            isSpeechRecognitionActive = false
        )

        // If we have recognized text when manually stopping, use it
        if (currentTempText.isNotEmpty()) {
            chatBotUiState = chatBotUiState.copy(
                userMessage = currentTempText,
                showRecordingDialog = false
            )
        }
    }

    private fun loadTodayMessages() {
        chatBotUiState = chatBotUiState.copy(
            isLoadingInitialData = true,
            isHistoryMode = false,
            currentDate = todayFormatted
        )

        viewModelScope.launch {
            botChatRepository.getTodayMessages().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        chatBotUiState = chatBotUiState.copy(
                            listOfMessages = result.data.toMutableList(),
                            isLoadingInitialData = false,
                            shouldScrollToBottom = true,
                            error = null
                        )
                    }
                    is ResultState.Failure -> {
                        chatBotUiState = chatBotUiState.copy(
                            isLoadingInitialData = false,
                            error = result.msg.localizedMessage
                        )
                    }
                    is ResultState.Loading -> {
                        chatBotUiState = chatBotUiState.copy(isLoadingInitialData = true)
                    }
                }
            }
        }
    }

    private fun loadMessagesByDate(date: String) {
        chatBotUiState = chatBotUiState.copy(
            isLoadingInitialData = true,
            isHistoryMode = true,
            currentDate = date
        )

        viewModelScope.launch {
            botChatRepository.getMessagesByDate(date).collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        chatBotUiState = chatBotUiState.copy(
                            listOfMessages = result.data.toMutableList(),
                            isLoadingInitialData = false,
                            shouldScrollToBottom = true,
                            error = null
                        )
                    }
                    is ResultState.Failure -> {
                        chatBotUiState = chatBotUiState.copy(
                            isLoadingInitialData = false,
                            error = result.msg.localizedMessage
                        )
                    }
                    is ResultState.Loading -> {
                        chatBotUiState = chatBotUiState.copy(isLoadingInitialData = true)
                    }
                }
            }
        }
    }

    private fun loadMoreMessages(date: String, oldestTimestamp: Long) {
        if (chatBotUiState.isLoadingMore) return

        chatBotUiState = chatBotUiState.copy(isLoadingMore = true)

        viewModelScope.launch {
            botChatRepository.loadMoreMessages(date, oldestTimestamp).collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        // If we got fewer items than PAGE_SIZE, we've reached the beginning
                        val hasMoreMessages = result.data.size >= ChatBotRepository.PAGE_SIZE

                        // Combine with existing messages
                        val updatedList = (result.data + chatBotUiState.listOfMessages).toMutableList()

                        chatBotUiState = chatBotUiState.copy(
                            listOfMessages = updatedList,
                            isLoadingMore = false,
                            hasMoreMessages = hasMoreMessages,
                            error = null
                        )
                    }
                    is ResultState.Failure -> {
                        chatBotUiState = chatBotUiState.copy(
                            isLoadingMore = false,
                            error = result.msg.localizedMessage
                        )
                    }
                    is ResultState.Loading -> {
                        chatBotUiState = chatBotUiState.copy(isLoadingMore = true)
                    }
                }
            }
        }
    }

    private fun loadAvailableDates() {
        viewModelScope.launch {
            botChatRepository.getAvailableChatDates().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        val formattedDates = result.data.associateWith { dateStr ->
                            formatDisplayDate(dateStr)
                        }

                        chatBotUiState = chatBotUiState.copy(
                            availableDates = result.data,
                            formattedAvailableDates = formattedDates
                        )
                    }
                    is ResultState.Failure -> {
                        Log.e("ChatBotViewModel", "Failed to load available dates: ${result.msg.message}")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun sendMessageWithImages(message: String, imageUris: List<Uri>) {
        chatBotUiState = chatBotUiState.copy(isSendingMessage = true)

        viewModelScope.launch {
            try {
                // Prepare pairs of Uri and byteArray
                val imageData = withContext(Dispatchers.IO) {
                    imageUris.mapNotNull { uri ->
                        try {
                            // Convert URI to bitmap and then to byte array
                            val bitmap = uriToBitmap(uri)
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                            val byteArray = byteArrayOutputStream.toByteArray()
                            Pair(uri, byteArray)
                        } catch (e: Exception) {
                            Log.e("ChatBotViewModel", "Failed to convert URI to byte array: ${e.message}")
                            null
                        }
                    }
                }

                // Save user message to Firestore with image data
                val imageUriList = imageData.map { it.first }
                val imageBytesList = imageData.map { it.second }

                // Save user message to Firestore with image data
                botChatRepository.saveMessage(
                    message = message,
                    role = ChatRoleEnum.USER.value,
                    imageUris = imageUriList,
                    imageBytes = imageBytesList
                ).collectLatest { result ->
                    when (result) {
                        is ResultState.Success -> {
                            val newList = chatBotUiState.listOfMessages
                            newList.add(result.data) // now with proper Firestore ID
                            chatBotUiState = chatBotUiState.copy(
                                listOfMessages = newList,
                                userMessage = "",
                                selectedImageUris = emptyList(),
                                shouldScrollToBottom = true
                            )
                        }

                        is ResultState.Failure -> {
                            chatBotUiState = chatBotUiState.copy(
                                isSendingMessage = false,
                                error = result.msg.localizedMessage
                            )
                            return@collectLatest
                        }

                        ResultState.Loading -> {
                            // Optional loading state
                        }
                    }
                }


                val chat = genAI.startChat()
                val updatedList = chatBotUiState.listOfMessages

                // Clear the input fields first
                chatBotUiState = chatBotUiState.copy(userMessage = "", selectedImageUris = emptyList())

                val response = if (imageData.isNotEmpty()) {
                    // Process images for Gemini
                    chat.sendMessage(
                        content {
                            for (pair in imageData) {
                                // Use the byte array directly for Gemini API
                                image(pair.second)
                            }
                            text(message)
                        }
                    )
                } else {
                    // Text-only content
                    chat.sendMessage(
                        content {
                            text(message)
                        }
                    )
                }

                val cleanedResponse = response.text?.replace("\\*\\*", "")?.trim() ?: ""

                if (cleanedResponse.isNotEmpty()) {
                    // Save bot response to Firestore
                    botChatRepository.saveMessage(
                        message = cleanedResponse,
                        role = ChatRoleEnum.MODEL.value
                    ).collectLatest { action->
                        when(action){
                            is ResultState.Success ->{
                                updatedList.add(action.data)
                            }
                            is ResultState.Failure -> {
                                chatBotUiState = chatBotUiState.copy(
                                    isSendingMessage = false,
                                    error = action.msg.localizedMessage
                                )
                                return@collectLatest
                            }
                            else->{

                            }
                        }

                    }
                }

                chatBotUiState = chatBotUiState.copy(
                    isSendingMessage = false,
                    listOfMessages = updatedList,
                    shouldScrollToBottom = true
                )

                // Refresh available dates
                loadAvailableDates()

            } catch (e: Exception) {
                Log.e("ChatBotViewModel", "Error sending message with images: ${e.message}", e)
                chatBotUiState = chatBotUiState.copy(isSendingMessage = false, error = e.localizedMessage)
            }
        }
    }
    fun formatDisplayDate(dateStr: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) // May 16, 2025
            val date = inputFormat.parse(dateStr)
            return outputFormat.format(date!!)
        } catch (e: Exception) {
            return dateStr // Return original if parsing fails
        }
    }


    private fun uriToBitmap(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(appContext.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(appContext.contentResolver, uri)
        }
    }
    override fun onCleared() {
        super.onCleared()
        speechRecognitionManager.destroy()
    }
}