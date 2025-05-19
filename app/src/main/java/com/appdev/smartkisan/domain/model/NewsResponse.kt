package com.appdev.smartkisan.domain.model

data class NewsResponse(
    val available: Int,
    val news: List<New>,
    val number: Int,
    val offset: Int
)