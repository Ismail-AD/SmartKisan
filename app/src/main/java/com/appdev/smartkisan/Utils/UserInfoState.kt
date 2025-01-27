package com.appdev.smartkisan.Utils

import com.appdev.smartkisan.data.UserEntity

sealed class UserInfoState {
    data object Loading : UserInfoState()
    data class Success(val user: UserEntity) : UserInfoState()
    data class Failure(val error: Throwable) : UserInfoState()
}