package com.appdev.smartkisan.Utils

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Functions {
    fun toConvertTime(chatTime: Long?): String {
        val spd = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return spd.format(Date(chatTime!!))
    }

    fun haveImagesChanged(currentUris: List<Uri>, initialUris: List<Uri>): Boolean {
        return !currentUris.all { it in initialUris }
    }
}