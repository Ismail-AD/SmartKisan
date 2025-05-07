package com.appdev.smartkisan.ViewModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Repository.NewsRepository
import com.appdev.smartkisan.States.NewsState
import com.appdev.smartkisan.Utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _newsState = MutableStateFlow(NewsState())
    val newsState: StateFlow<NewsState> = _newsState.asStateFlow()

    init {
       fetchAgricultureNews()
    }
    fun fetchAgricultureNews() {
        viewModelScope.launch {
            newsRepository.getAgricultureNews().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _newsState.update { it.copy(isLoading = true) }
                    }

                    is ResultState.Success -> {
                        val newsResponse = result.data

                        Log.d("NewsViewModel", "Fetched ${newsResponse.news.size} articles")
                        Log.d("NewsViewModel", "Fetched ${newsResponse.news} articles")

                        _newsState.update {
                            it.copy(
                                isLoading = false,
                                articles = newsResponse.news,
                                statusMessage = if (newsResponse.news.isEmpty())
                                    "No agriculture news available"
                                else ""
                            )
                        }
                    }

                    is ResultState.Failure -> {
                        val errorMessage = result.msg.message ?: "Unknown error occurred"
                        Log.e("NewsViewModel", "Error fetching news: $errorMessage")

                        _newsState.update {
                            it.copy(
                                isLoading = false,
                                statusMessage = "Error: $errorMessage"
                            )
                        }
                    }
                }
            }
        }
    }

    // Refresh news data
    fun refreshNews() {
        fetchAgricultureNews()
    }
}