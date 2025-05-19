package com.appdev.smartkisan.presentation.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.appdev.smartkisan.domain.model.Product
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ProductNavType : NavType<com.appdev.smartkisan.domain.model.Product>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): com.appdev.smartkisan.domain.model.Product? {
        return bundle.getString(key)?.let { json ->
            Json.decodeFromString<com.appdev.smartkisan.domain.model.Product>(json)
        }
    }

    override fun parseValue(value: String): com.appdev.smartkisan.domain.model.Product {
        return Json.decodeFromString<com.appdev.smartkisan.domain.model.Product>(value)
    }

    override fun put(bundle: Bundle, key: String, value: com.appdev.smartkisan.domain.model.Product) {
        bundle.putString(key, Json.encodeToString(value))
    }
}