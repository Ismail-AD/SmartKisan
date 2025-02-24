package com.appdev.smartkisan.Utils

import android.content.Context
import android.content.SharedPreferences

object SessionManagement {

    private const val PREFS_NAME = "user_session_prefs"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    private const val KEY_USER_TYPE = "userType"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_EXPIRES_AT = "expires_at"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"

    private const val EXPIRY_MARGIN = 300

    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setOnboardingCompleted(context: Context, isCompleted: Boolean) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, isCompleted)
            .apply()
    }

    fun isOnboardingCompleted(context: Context): Boolean {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }


    fun saveUserId(context: Context, userId: String) {
        getSharedPreferences(context).edit().apply {
            putString("user_id", userId)
            apply()
        }
    }

    fun saveUserType(context: Context, userType: String) {
        getSharedPreferences(context).edit().apply {
            putString("user_type", userType)
            apply()
        }
    }

    fun saveAccessToken(context: Context, accessToken: String) {
        getSharedPreferences(context).edit().apply {
            putString("access_token", accessToken)
            apply()
        }
    }


    fun getUserType(context: Context): String? =
        getSharedPreferences(context).getString("user_type", null)



    fun clearSession(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit()
            .clear()
            .apply()
    }


    fun saveSession(
        context: Context,
        accessToken: String,
        refreshToken: String,
        expiresAt: Long,
        userId: String,
        userEmail: String
    ) {
        getSharedPreferences(context).edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_EXPIRES_AT, expiresAt)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, userEmail)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? =
        getSharedPreferences(context).getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(context: Context): String? =
        getSharedPreferences(context).getString(KEY_REFRESH_TOKEN, null)

    fun getExpiresAt(context: Context): Long =
        getSharedPreferences(context).getLong(KEY_EXPIRES_AT, 0)

    fun getUserId(context: Context): String? =
        getSharedPreferences(context).getString(KEY_USER_ID, null)

    fun isSessionValid(context: Context): Boolean {
        val accessToken = getAccessToken(context)
        val expiresAt = getExpiresAt(context)
        val currentTime = System.currentTimeMillis() / 1000

        return if (accessToken != null) {
            // Session is valid if current time is less than expiry time minus margin
            currentTime < (expiresAt - EXPIRY_MARGIN)
        } else {
            false
        }
    }
}