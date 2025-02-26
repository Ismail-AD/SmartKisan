package com.appdev.smartkisan.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.appdev.smartkisan.data.Product
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ProductNavType : NavType<Product>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Product? {
        return bundle.getString(key)?.let { json ->
            Json.decodeFromString<Product>(json)
        }
    }

    override fun parseValue(value: String): Product {
        return Json.decodeFromString<Product>(value)
    }

    override fun put(bundle: Bundle, key: String, value: Product) {
        bundle.putString(key, Json.encodeToString(value))
    }
}