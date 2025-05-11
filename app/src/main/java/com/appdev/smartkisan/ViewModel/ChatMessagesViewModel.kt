package com.appdev.smartkisan.ViewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.ChatActions
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.Repository.UserChatsRepository
import com.appdev.smartkisan.States.ChatUiState
import com.appdev.smartkisan.Utils.MessageStatus
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.data.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserChatViewModel @Inject constructor(
    private val userChatsRepository: UserChatsRepository,
    private val sessionManagement: SessionManagement,
    private val repository: Repository,
    private val appContext: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    val currentUserId = userChatsRepository.getCurrentUserId() ?: ""
    init {
        setUserData()
    }

    fun setUserData() {
        _state.value = _state.value.copy(
            userName = getUserName(),
            userImage = getProfileImage()
        )
    }

    fun getUserName(): String = sessionManagement.getUserName() ?: ""

    fun getProfileImage(): String = sessionManagement.getUserImage() ?: ""

    fun onAction(action: ChatActions) {
        when (action) {
            is ChatActions.SetReceiverInfo -> {
                _state.value = _state.value.copy(
                    receiverId = action.receiverId,
                    receiverName = action.receiverName,
                    receiverProfilePic = action.receiverProfilePic
                )
                if (_state.value.messages.isEmpty()) {
                    loadMessages(action.receiverId)
                }
            }

            is ChatActions.SendMessage -> {
                if (action.content.isNotBlank() || _state.value.selectedImageUris.isNotEmpty()) {
                    sendMessageWithImages(
                        content = action.content,
                        myName = action.myName,
                        myImage = action.myImage
                    )
                }
            }

            is ChatActions.UpdateMessageInput -> {
                _state.value = _state.value.copy(messageInput = action.input)
            }


            is ChatActions.AddSelectedImages -> {
                _state.value = _state.value.copy(
                    selectedImageUris = _state.value.selectedImageUris + action.uris
                )
            }

            is ChatActions.RemoveImage -> {
                _state.value = _state.value.copy(
                    selectedImageUris = _state.value.selectedImageUris.filter { it != action.uri }
                )
            }

            is ChatActions.DeleteChat -> {
                action.receiverId?.let { deleteChat(it) }
            }

            else -> Unit
        }
    }

    private fun sendMessageWithImages(content: String, myName: String, myImage: String) {
        if (content.isBlank() && _state.value.selectedImageUris.isEmpty() || _state.value.receiverId.isNullOrBlank()) return

        // Save images before clearing the UI
        val imagesToSend = _state.value.selectedImageUris.toList()

        // Generate a temporary message ID for this pending message
        val tempMessageId = UUID.randomUUID().toString()
        val currentTimeMillis = System.currentTimeMillis()

        // Create a pending message and add it to the messages list
        val pendingMessage = ChatMessage(
            messageId = tempMessageId,
            message = content,
            senderID = currentUserId,
            timeStamp = currentTimeMillis,
            isRead = false,
            status = MessageStatus.PENDING.name,
            imageUrls = imagesToSend.map { it.toString() },
        )

        // Update state to include the pending message
        _state.value = _state.value.copy(
            messageInput = "",
            isSending = true,
            selectedImagesInProgress = imagesToSend,
            selectedImageUris = emptyList(), // Clear images from UI immediately
            messages = _state.value.messages + pendingMessage // Add pending message to the list
        )

        viewModelScope.launch {
            try {
                val imageData = withContext(Dispatchers.IO) {
                    _state.value.selectedImagesInProgress.mapNotNull { uri ->
                        try {
                            val bitmap = uriToBitmap(uri)
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                            val byteArray = byteArrayOutputStream.toByteArray()
                            Pair(uri, byteArray)
                        } catch (e: Exception) {
                            Log.e("UserChatViewModel", "Failed to convert URI to byte array: ${e.message}")
                            null
                        }
                    }
                }

                val imageUris = imageData.map { it.first }
                val imageBytes = imageData.map { it.second }

                _state.value.receiverId?.let { receiverId ->
                    userChatsRepository.sendMessage(
                        myName = myName,
                        myImage = myImage,
                        receiverId = receiverId,
                        receiverName = _state.value.receiverName ?: "",
                        receiverProfilePic = _state.value.receiverProfilePic,
                        messageContent = content,
                        imageUris = imageUris,
                        imageBytes = imageBytes,
                        tempMessageId
                    ).collect { result ->
                        when (result) {
                            is ResultState.Success -> {
                                // Update message status from pending to sent
                                val updatedMessages = _state.value.messages.map { message ->
                                    if (message.messageId == result.data.second) {
                                        // Replace with the actual message from result if available
                                       result.data.first
                                    } else {
                                        message
                                    }
                                }

                                _state.value = _state.value.copy(
                                    isSending = false,
                                    error = null,
                                    selectedImageUris = emptyList(),
                                    messages = updatedMessages
                                )
                            }

                            is ResultState.Failure -> {
                                // Mark message as failed
                                val updatedMessages = _state.value.messages.map { message ->
                                    if (message.messageId == tempMessageId) {
                                        message.copy(status = MessageStatus.PENDING.name)
                                    } else {
                                        message
                                    }
                                }

                                _state.value = _state.value.copy(
                                    isSending = false,
                                    error = result.msg.localizedMessage,
                                    messages = updatedMessages
                                )
                            }

                            ResultState.Loading -> {
                                _state.value = _state.value.copy(isSending = true)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("UserChatViewModel", "Error sending message with images: ${e.message}", e)

                // Mark message as failed
                val updatedMessages = _state.value.messages.map { message ->
                    if (message.messageId == tempMessageId) {
                        message.copy(status = "failed")
                    } else {
                        message
                    }
                }

                _state.value = _state.value.copy(
                    isSending = false,
                    error = e.localizedMessage,
                    messages = updatedMessages
                )
            }
        }
    }

    private fun loadMessages(receiverId: String) {
        Log.e("AZXQW","LOADING CALLED")

        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {

//            _state.value = _state.value.copy(isLoading = true)

            try {
                userChatsRepository.loadMessages(receiverId).collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            _state.value = _state.value.copy(
                                messages = result.data,
                                isLoading = false,
                                error = null
                            )
                        }

                        is ResultState.Failure -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = result.msg.localizedMessage
                            )
                        }

                        ResultState.Loading -> {
//                            _state.value = _state.value.copy(isLoading = true)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("UserChatViewModel", "Error in loadMessages flow: ${e.message}", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
            }
        }
    }

    private fun deleteChat(receiverId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isDeleting = true)

            try {
                userChatsRepository.deleteChat(receiverId).collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            _state.value = _state.value.copy(
                                isDeleting = false,
                                error = null,
                                isChatDeleted = true
                            )
                        }

                        is ResultState.Failure -> {
                            _state.value = _state.value.copy(
                                isDeleting = false,
                                error = result.msg.localizedMessage
                            )
                        }

                        ResultState.Loading -> {
                            _state.value = _state.value.copy(isDeleting = true)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("UserChatViewModel", "Error in deleteChat flow: ${e.message}", e)
                _state.value = _state.value.copy(
                    isDeleting = false,
                    error = e.localizedMessage
                )
            }
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
}