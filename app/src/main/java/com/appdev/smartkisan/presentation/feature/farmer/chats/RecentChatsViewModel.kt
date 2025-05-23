package com.appdev.smartkisan.presentation.feature.farmer.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.data.repository.Repository
import com.appdev.smartkisan.data.repository.UserChatsRepository
import com.appdev.smartkisan.Utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RecentChatsViewModel @Inject constructor(
    val chatRepository: UserChatsRepository,
    val repository: Repository
) : ViewModel() {
    private val _state = MutableStateFlow(ChatListUiState())
    val state: StateFlow<ChatListUiState> = _state.asStateFlow()

    init {
        // Load initial data
        onAction(ChatListActions.GetMyMessages)
    }

    fun deleteChat(receiverId: String) {
        viewModelScope.launch {
            chatRepository.deleteChat(receiverId).collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        // The chat is now deleted and the real-time listener should update the UI
                        _state.value = _state.value.copy(error = null)
                    }

                    is ResultState.Failure -> {
                        _state.value  = _state.value.copy(error = result.msg.localizedMessage)
                    }

                    else -> {}
                }
            }
        }
    }

    fun onAction(action: ChatListActions) {
        when (action) {
            is ChatListActions.CurrentSelectedTab -> {
                _state.value = _state.value.copy(currentTab = action.selectedTab)
                if (action.selectedTab == 0) {
                    loadRecentChats()
                } else {
                    fetchSellersProfiles()
                }
            }

            is ChatListActions.SearchChats -> {
                searchChats(action.query)
            }

            is ChatListActions.UpdateQuery -> {
                _state.value = _state.value.copy(query = action.query)
            }

            ChatListActions.GetChatWithList -> {
                fetchSellersProfiles()
            }

            ChatListActions.GetMyMessages -> {
                loadRecentChats()
            }

            is ChatListActions.DeleteChat ->{
                deleteChat(action.receiverId)
            }
            else -> {

            }
        }
    }

    private fun fetchSellersProfiles() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            repository.getSellersProfile().collect { result ->
                _state.value = when (result) {
                    is ResultState.Success -> {
                        _state.value.copy(
                            chatWithList = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is ResultState.Failure -> {
                        _state.value.copy(
                            isLoading = false,
                            error = result.msg.localizedMessage
                        )
                    }

                    ResultState.Loading -> {
                        _state.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }
    }


    private fun searchChats(query: String) {
        if (query.isEmpty()) {
            _state.update { it.copy(filteredChats = emptyList()) }
            return
        }

        val filteredList = _state.value.recentMessages.filter { chatMate ->
            // Safe call operator for nullable receiverName
            (chatMate.receiverName?.contains(query, ignoreCase = true) == true) ||
                    (chatMate.lastMessage?.contains(query, ignoreCase = true) == true)
        }

        _state.update { it.copy(filteredChats = filteredList) }
    }


    private fun loadRecentChats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            chatRepository.getRecentChats().collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        _state.update {
                            it.copy(
                                recentMessages = result.data,
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