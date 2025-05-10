package com.appdev.smartkisan.Utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManagement @Inject constructor(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_USER_TYPE = "user_type"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_EXPIRES_AT = "expires_at"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_IMAGE = "user_image"

        private const val EXPIRY_MARGIN = 300
    }

    fun setOnboardingCompleted(isCompleted: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, isCompleted)
            .apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_ID, userId)
            apply()
        }
    }

    fun saveUserType(userType: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_TYPE, userType)
            apply()
        }
    }

    fun saveUserName(userName: String, userImage: String? = null) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_NAME, userName)
            if (userImage != null) {
                putString(KEY_USER_IMAGE, userImage)
            }
            apply()
        }
    }

    fun saveUserImage(userImage: String?) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_IMAGE, userImage)
            apply()
        }
    }

    fun getUserName(): String? =
        sharedPreferences.getString(KEY_USER_NAME, null)

    fun getUserImage(): String? =
        sharedPreferences.getString(KEY_USER_IMAGE, null)

    fun saveAccessToken(accessToken: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            apply()
        }
    }

    fun getUserType(): String? =
        sharedPreferences.getString(KEY_USER_TYPE, null)

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }

    fun saveSession(
        accessToken: String,
        refreshToken: String,
        expiresAt: Long,
        userId: String,
        userEmail: String
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_EXPIRES_AT, expiresAt)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, userEmail)
            apply()
        }
    }

    fun getAccessToken(): String? =
        sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? =
        sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    fun getExpiresAt(): Long =
        sharedPreferences.getLong(KEY_EXPIRES_AT, 0)

    fun getUserId(): String? =
        sharedPreferences.getString(KEY_USER_ID, null)

    fun isSessionValid(): Boolean {
        val accessToken = getAccessToken()
        val expiresAt = getExpiresAt()
        val currentTime = System.currentTimeMillis() / 1000

        return if (accessToken != null) {
            // Session is valid if current time is less than expiry time minus margin
            currentTime < (expiresAt - EXPIRY_MARGIN)
        } else {
            false
        }
    }
}