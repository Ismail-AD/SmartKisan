package com.appdev.smartkisan.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.ChatBotScreenActions
import com.appdev.smartkisan.Actions.HomeScreenActions
import com.appdev.smartkisan.BuildConfig
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.States.ChatBotUiState
import com.appdev.smartkisan.States.UserAuthState
import com.appdev.smartkisan.data.Chat
import com.appdev.smartkisan.data.ChatMessage
import com.appdev.smartkisan.data.ChatRoleEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatBotViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    var chatBotUiState by mutableStateOf(ChatBotUiState())
        private set

    private val genAI by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_KEY
        )
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

            is ChatBotScreenActions.ClearSelectedImages -> {
                chatBotUiState = chatBotUiState.copy(selectedImageUris = emptyList())
            }

            is ChatBotScreenActions.RemoveImage -> {
                chatBotUiState =
                    chatBotUiState.copy(selectedImageUris = chatBotUiState.selectedImageUris.filter { uri -> uri != action.uri })
            }

            is ChatBotScreenActions.OpenImagePicker -> {

            }

            is ChatBotScreenActions.SendPrompt -> {
                if (action.prompt.isNotBlank()) {
                    try {
                        chatBotUiState =
                            chatBotUiState.copy(listOfMessages = chatBotUiState.listOfMessages.apply {
                                add(
                                    Chat(
                                        message = action.prompt,
                                        role = ChatRoleEnum.USER.value,
                                        timeStamp = System.currentTimeMillis()
                                    )
                                )
                            })
                        sendMessage(action.prompt)
                    } catch (e: Exception) {
                        chatBotUiState =
                            chatBotUiState.copy(error = e.localizedMessage, isLoading = false)
                    }
                }
            }

            else -> {}
        }
    }

    private fun sendMessage(message: String) {
        chatBotUiState = chatBotUiState.copy(isLoading = true)
        try {
            viewModelScope.launch {
                val chat = genAI.startChat()
                val updatedList = chatBotUiState.listOfMessages
                val response =  chat.sendMessage(
                    content(ChatRoleEnum.USER.value) {
                        text(message)
                        chatBotUiState =
                            chatBotUiState.copy(userMessage = "", selectedImageUris = emptyList())
                    }
                )

                val cleanedResponse = response.text?.replace("\\*\\*", "")?.trim() ?: ""

                if (cleanedResponse.isNotEmpty()) {
                    updatedList.add(
                        Chat(
                            message = cleanedResponse,
                            role = ChatRoleEnum.MODEL.value,
                            timeStamp = System.currentTimeMillis()
                        )
                    )
                }
                chatBotUiState =
                    chatBotUiState.copy(isLoading = false, listOfMessages = updatedList)
            }
        } catch (e: Exception) {
            chatBotUiState = chatBotUiState.copy(isLoading = false, error = e.localizedMessage)
        }
    }

}