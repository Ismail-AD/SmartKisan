package com.appdev.smartkisan.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userInfo_table")
data class UserInfo(
    val accessToken: String? = "",
    @PrimaryKey(autoGenerate = false)
    val userId: String = ""
)
