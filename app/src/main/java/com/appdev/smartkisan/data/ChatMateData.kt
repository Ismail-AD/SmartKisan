package com.appdev.smartkisan.data

import com.appdev.smartkisan.R

data class ChatMateData(
    val profileImage: Int = R.drawable.agribot,
    var profileImageUrl: String = "",
    val username: String? = "",
    val userid: String? = "",
    val userContact: String? = "",
    var lastMsg: String? = "",
    var chatTime: Long? = 0,
)
