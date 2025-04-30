package com.appdev.smartkisan.Utils

enum class MessageStatus {
    SENT,    // Message has been sent but not delivered
    DELIVERED, // Message has been delivered to the recipient's device
    READ     // Message has been read by the recipient
}