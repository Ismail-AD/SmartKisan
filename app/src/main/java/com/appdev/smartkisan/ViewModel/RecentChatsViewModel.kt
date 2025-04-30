package com.appdev.smartkisan.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.ChatListActions
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.Repository.UserChatsRepository
import com.appdev.smartkisan.States.ChatListUiState
import com.appdev.smartkisan.Utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RecentChatsViewModel @Inject constructor(val chatRepository: UserChatsRepository,val repository: Repository) : ViewModel() {
    private val _state = MutableStateFlow(ChatListUiState())
    val state: StateFlow<ChatListUiState> = _state.asStateFlow()

    init {
        // Load initial data
        onAction(ChatListActions.GetMyMessages)
    }

    fun onAction(action: ChatListActions) {
        when (action) {
            is ChatListActions.CurrentSelectedTab -> {
                _state.value = _state.value.copy(currentTab = action.selectedTab)
                if (action.selectedTab == 0) {
                    onAction(ChatListActions.GetMyMessages)
                } else {
                    onAction(ChatListActions.GetChatWithList)
                }
            }

            ChatListActions.GetChatWithList -> {
                fetchSellersProfiles()
            }

            ChatListActions.GetMyMessages -> {
                loadRecentChats()
            }

            else->{

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