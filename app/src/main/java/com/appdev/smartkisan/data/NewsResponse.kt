package com.appdev.smartkisan.data

data class NewsResponse(
    val available: Int,
    val news: List<New>,
    val number: Int,
    val offset: Int
)