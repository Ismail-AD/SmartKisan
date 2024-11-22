package com.appdev.smartkisan.Utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Functions {
    fun toConvertTime(chatTime: Long?): String {
        val spd = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return spd.format(Date(chatTime!!))
    }
}