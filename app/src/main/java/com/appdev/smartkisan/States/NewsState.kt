package com.appdev.smartkisan.States

import com.appdev.smartkisan.data.New

data class NewsState(
    val isLoading: Boolean = false,
    val articles: List<New> = emptyList(),
    val statusMessage: String = "Tap button below to get agriculture news"
)