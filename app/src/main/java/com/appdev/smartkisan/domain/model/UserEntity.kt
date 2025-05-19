package com.appdev.smartkisan.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("id") var id: String? = null,
    @SerialName("name") var name: String = "",
    @SerialName("imageurl") var imageUrl: String? = null,
    @SerialName("email") var email: String = "",
    @SerialName("role") var role: String = ""
)