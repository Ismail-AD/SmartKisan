package com.appdev.smartkisan.Utils

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Utility functions for date and time formatting in the application
 */
object DateTimeUtils {

    /**
     * Extracts only the date part from a datetime string in format "yyyy-MM-dd HH:mm:ss"
     *
     * @param dateTimeString The original datetime string
     * @return Only the date part or empty string if input is invalid
     */
    fun extractDateOnly(dateTimeString: String?): String {
        if (dateTimeString.isNullOrEmpty()) return ""

        return try {
            dateTimeString.split(" ").firstOrNull() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Formats a time string from 24-hour format to 12-hour format with AM/PM
     *
     * @param timeString Time in format "HH:mm:ss"
     * @return Formatted time string in 12-hour format with AM/PM
     */
    fun formatTo12HourTime(timeString: String): String {
        return try {
            val parts = timeString.split(":")
            if (parts.size < 2) return timeString

            val hour = parts[0].toInt()
            val minute = parts[1]
            val second = if (parts.size > 2) parts[2] else "00"

            val amPm = if (hour >= 12) "PM" else "AM"
            val hour12 = when (hour) {
                0 -> "12"
                in 1..12 -> hour.toString()
                else -> (hour - 12).toString()
            }

            "$hour12:$minute${if (second.isNotEmpty()) ":$second" else ""} $amPm"
        } catch (e: Exception) {
            timeString // Return original string if parsing fails
        }
    }

    /**
     * Formats a full datetime string to separate date and 12-hour time format
     *
     * @param dateTimeString Datetime string in format "yyyy-MM-dd HH:mm:ss"
     * @return Pair of (date, formatted time) or null if parsing fails
     */
    fun formatDateTime(dateTimeString: String?): Pair<String, String>? {
        if (dateTimeString.isNullOrEmpty()) return null

        return try {
            val parts = dateTimeString.split(" ")
            if (parts.size < 2) return Pair(dateTimeString, "")

            val date = parts[0]
            val time = formatTo12HourTime(parts[1])

            Pair(date, time)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Gets a nicely formatted date string (e.g., "May 7, 2025") from "yyyy-MM-dd" format
     *
     * @param dateString Date in format "yyyy-MM-dd"
     * @return Formatted date string or original string if parsing fails
     */
    fun getNiceDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}