package com.appdev.smartkisan.presentation.feature.farmer.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.data.repository.NewsRepository
import com.appdev.smartkisan.Utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _newsState = MutableStateFlow(NewsState())
    val newsState: StateFlow<NewsState> = _newsState.asStateFlow()

    // Set a minimum threshold for showing "Load More" button
    private val MIN_REMAINING_FOR_LOAD_MORE = 3

    init {
        fetchAgricultureNews(isInitial = true)
    }

    private fun fetchAgricultureNews(isInitial: Boolean = false) {
        val today = LocalDate.now()
        val yesterday = today.minusDays(2)

        val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val earliest = yesterday.format(isoFormatter)
        val latest = today.format(isoFormatter)

        // Use current offset if loading more, otherwise reset to 0
        val offset = if (isInitial) 0 else _newsState.value.currentOffset

        viewModelScope.launch {
            // If initial load or refresh, show full loading indicator
            // If loading more, show the "load more" indicator at the bottom
            if (isInitial) {
                _newsState.update { it.copy(isLoading = true) }
            } else {
                _newsState.update { it.copy(isLoadingMore = true) }
            }

            newsRepository
                .getAgricultureNews(earliest, latest, offset)
                .collect { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            // Loading state is already set above
                        }

                        is ResultState.Success -> {
                            val fmtDate = today.format(
                                DateTimeFormatter.ofPattern("MMMM d, yyyy")
                            )

                            // Calculate if more news are available
                            val totalAvailable = result.data.available
                            val currentCount = if (isInitial) {
                                result.data.news.size
                            } else {
                                _newsState.value.articles.size + result.data.news.size
                            }

                            // Only show "Load More" if there's a significant number of items remaining
                            val remainingItems = totalAvailable - currentCount
                            val hasMoreNews = remainingItems > 0 && remainingItems >= MIN_REMAINING_FOR_LOAD_MORE

                            // If there are just a few items left (less than threshold), automatically load them
                            val shouldAutoLoadRemainder = remainingItems > 0 && remainingItems < MIN_REMAINING_FOR_LOAD_MORE

                            // Update news list - either replace or append
                            val updatedNewsList = if (isInitial) {
                                result.data.news
                            } else {
                                _newsState.value.articles + result.data.news
                            }

                            // Calculate new offset for next page
                            val newOffset = offset + result.data.number

                            _newsState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoadingMore = false,
                                    articles = updatedNewsList,
                                    currentDate = fmtDate,
                                    currentOffset = newOffset,
                                    totalAvailable = totalAvailable,
                                    hasMoreNews = hasMoreNews,
                                    statusMessage = if (result.data.news.isEmpty() && isInitial)
                                        "No agriculture news available for $earliest"
                                    else ""
                                )
                            }

                            // Automatically load the remaining few items if needed
                            if (shouldAutoLoadRemainder && !isInitial) {
                                fetchAgricultureNews(isInitial = false)
                            }
                        }

                        is ResultState.Failure -> {
                            _newsState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoadingMore = false,
                                    statusMessage = "Error: ${result.msg.localizedMessage}"
                                )
                            }
                        }
                    }
                }
        }
    }

    // Refresh news data completely (reset pagination)
    fun refreshNews() {
        fetchAgricultureNews(isInitial = true)
    }

    // Load more news (pagination)
    fun loadMoreNews() {
        // Only load more if we're not already loading and there are more news to load
        if (!_newsState.value.isLoading &&
            !_newsState.value.isLoadingMore &&
            _newsState.value.hasMoreNews) {
            fetchAgricultureNews(isInitial = false)
        }
    }
}