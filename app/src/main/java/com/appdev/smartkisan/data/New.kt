package com.appdev.smartkisan.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class New(
    val authors: List<String>? = null,
    val category: String? = null,
    val id: Long? = null,
    val image: String? = null,
    val language: String? = null,
    val publish_date: String? = null,
    val sentiment: Double? = null,
    val source_country: String? = null,
    val summary: String? = null,
    val text: String? = null,
    val title: String? = null,
    val url: String? = null,
    val video: String? = null
)
 : Parcelable