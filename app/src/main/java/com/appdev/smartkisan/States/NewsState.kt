package com.appdev.smartkisan.States

import com.appdev.smartkisan.data.New

data class NewsState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false, // New flag for pagination loading
    val articles: List<New> = emptyList(),
    val statusMessage: String = "",
    val currentDate: String = "",
    val currentOffset: Int = 0, // Track current pagination offset
    val totalAvailable: Int = 0, // Total available news from API
    val hasMoreNews: Boolean = true // Flag to determine if more news can be loaded
)