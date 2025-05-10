package com.appdev.smartkisan.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.ChatActions
import com.appdev.smartkisan.Repository.UserChatsRepository
import com.appdev.smartkisan.States.ChatUiState
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.Utils.SessionManagement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatMessagesViewModel @Inject constructor(val chatRepository: UserChatsRepository,val sessionManagement: SessionManagement) :
    ViewModel() {
    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()
    val currentUserId = chatRepository.getCurrentUserId() ?: ""

    init {
        setUserData()
    }
    fun setUserData() {
        _state.value = _state.value.copy(userName = getUserName(), userImage = getProfileImage())
    }
    fun getUserName():String{
        return sessionManagement.getUserName() ?:""
    }
    fun getProfileImage():String{
        return sessionManagement.getUserImage() ?:""
    }


    fun onAction(action: ChatActions) {
        when (action) {

            is ChatActions.SetReceiverInfo -> {
                _state.update {
                    it.copy(
                        receiverId = action.receiverId,
                        receiverName = action.receiverName,
                        receiverProfilePic = action.receiverProfilePic
                    )
                }
                if (_state.value.messages.isEmpty()) {
                    loadMessages(action.receiverId)
                }
            }

            is ChatActions.SendMessage -> {
                sendMessage(action.content,action.myName,action.myImage)
            }

            is ChatActions.UpdateMessageInput -> {
                _state.update { it.copy(messageInput = action.input) }
            }

            is ChatActions.LoadMessages -> {
                _state.value.receiverId?.let { loadMessages(it) }
            }

            is ChatActions.AddSelectedImages -> {
                _state.update {
                    it.copy(selectedImageUris = _state.value.selectedImageUris + action.uris)
                }
            }

            is ChatActions.RemoveImage -> {
                _state.update {
                    it.copy(selectedImageUris = _state.value.selectedImageUris.filter { uri -> uri != action.uri })
                }
            }

            else -> {

            }
        }
    }


    private fun sendMessage(content: String, myName: String, myImage: String) {
        if (content.isBlank() || _state.value.receiverId.trim().isEmpty()) return
        _state.update { it.copy(messageInput = "") }
        viewModelScope.launch {


            _state.value.receiverId?.let { receiverId ->

                chatRepository.sendMessage(myName,myImage,receiverId,state.value.receiverName,state.value.receiverProfilePic, content).collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            // Clear input field after successful sending
                            _state.update {
                                it.copy(
                                    isSending = false,
                                    error = null
                                )
                            }
                        }

                        is ResultState.Failure -> {
                            _state.update {
                                it.copy(
                                    isSending = false,
                                    error = result.msg.localizedMessage
                                )
                            }
                        }

                        ResultState.Loading -> {
                            _state.update { it.copy(isSending = true) }
                        }
                    }
                }
            }
        }
    }

    private fun loadMessages(receiverId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            chatRepository.loadMessages(receiverId).collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        _state.update {
                            it.copy(
                                messages = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is ResultState.Failure -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.msg.localizedMessage
                            )
                        }
                    }

                    ResultState.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }


}