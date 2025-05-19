package com.appdev.smartkisan.presentation.Resuable

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.appdev.smartkisan.domain.model.Chat
import com.appdev.smartkisan.domain.model.ChatRoleEnum

// Define your custom colors here - adjust as needed to match your app
private val myGreen = Color(0xFF68BB59)
private val lightBlue = Color(0xFFE6F7F9)

@Composable
fun MessageItem(chat: com.appdev.smartkisan.domain.model.Chat) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, end = 10.dp, start = 10.dp),
        horizontalAlignment = if (chat.role == com.appdev.smartkisan.domain.model.ChatRoleEnum.USER.value) Alignment.End else Alignment.Start
    ) {
        // First display images (if present and it's a user message)
        if (chat.images.isNotEmpty() && chat.role == com.appdev.smartkisan.domain.model.ChatRoleEnum.USER.value) {
            when (chat.images.size) {
                1 -> {
                    // Single image
                    AsyncImage(
                        model = chat.images.first(),
                        contentDescription = "Attached image",
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .widthIn(max = 300.dp)
                            .heightIn(max = 200.dp)
                            .clip(
                                RoundedCornerShape(
                                    topEnd = 0.dp,
                                    topStart = 10.dp,
                                    bottomEnd = 10.dp,
                                    bottomStart = 10.dp
                                )
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
                else -> {
                    // Multiple images in a horizontal scrollable row
                    LazyRow(
                        modifier = Modifier.padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(chat.images) { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Attached image",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }

        // Display message text if not empty - using your existing style
        if (chat.message.isNotBlank()) {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .background(
                        if (chat.role == com.appdev.smartkisan.domain.model.ChatRoleEnum.USER.value) myGreen else if (isSystemInDarkTheme()) Color(
                            0xFFAFD7DC
                        ) else lightBlue,
                        RoundedCornerShape(
                            topEnd = if (chat.role == com.appdev.smartkisan.domain.model.ChatRoleEnum.USER.value) 0.dp else 10.dp,
                            topStart = if (chat.role == com.appdev.smartkisan.domain.model.ChatRoleEnum.USER.value) 10.dp else 0.dp,
                            bottomEnd = 10.dp,
                            bottomStart = 10.dp
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatMessageWithBoldText(chat.message),
                    fontSize = 15.sp,
                    color = if (chat.role == com.appdev.smartkisan.domain.model.ChatRoleEnum.USER.value) Color.White else Color.Black.copy(
                        alpha = 0.9f
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }
}

fun formatMessageWithBoldText(message: String): AnnotatedString {
    return buildAnnotatedString {
        val segments = message.split("**")
        segments.forEachIndexed { index, segment ->
            if (index % 2 == 0) {
                append(segment)
            } else {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(segment)
                }
            }
        }
    }
}