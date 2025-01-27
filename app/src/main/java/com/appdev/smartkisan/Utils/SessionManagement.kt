package com.appdev.smartkisan.Utils

import android.content.Context
import android.content.SharedPreferences

object SessionManagement {

    private const val PREFS_NAME = "user_session_prefs"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    private const val KEY_USER_TYPE = "userType"

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


    fun getUserId(context: Context): String? =
        getSharedPreferences(context).getString("user_id", null)

    fun getUserType(context: Context): String? =
        getSharedPreferences(context).getString("user_type", null)

    fun getAccessToken(context: Context): String? =
        getSharedPreferences(context).getString("access_token", null)


    fun clearSession(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}