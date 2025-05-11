package com.appdev.smartkisan.ui.OtherComponents

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.appdev.smartkisan.data.BotChatMessage
import com.appdev.smartkisan.data.ChatRoleEnum

// Define your custom colors here - adjust as needed to match your app
private val myGreen = Color(0xFF68BB59)
private val lightBlue = Color(0xFFE6F7F9)

@Composable
fun BotMessageItem(botMessage: BotChatMessage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, end = 10.dp, start = 10.dp),
        horizontalAlignment = if (botMessage.role == ChatRoleEnum.USER.value) Alignment.End else Alignment.Start
    ) {
        // First display images (if present and it's a user message)
        if (botMessage.imageUrls.isNotEmpty() && botMessage.role == ChatRoleEnum.USER.value) {
            when (botMessage.imageUrls.size) {
                1 -> {
                    // Single image
                    AsyncImage(
                        model = botMessage.imageUrls.first(),
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
                        items(botMessage.imageUrls) { uri ->
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
        if (botMessage.message.isNotBlank()) {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .background(
                        if (botMessage.role == ChatRoleEnum.USER.value) myGreen else if (isSystemInDarkTheme()) Color(
                            0xFFAFD7DC
                        ) else lightBlue,
                        RoundedCornerShape(
                            topEnd = if (botMessage.role == ChatRoleEnum.USER.value) 0.dp else 10.dp,
                            topStart = if (botMessage.role == ChatRoleEnum.USER.value) 10.dp else 0.dp,
                            bottomEnd = 10.dp,
                            bottomStart = 10.dp
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatMessageWithBoldText(botMessage.message),
                    fontSize = 15.sp,
                    color = if (botMessage.role == ChatRoleEnum.USER.value) Color.White else Color.Black.copy(
                        alpha = 0.9f
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }
}
