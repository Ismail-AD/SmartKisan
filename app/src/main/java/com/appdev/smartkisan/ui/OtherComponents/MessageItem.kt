package com.appdev.smartkisan.ui.OtherComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdev.smartkisan.data.Chat
import com.appdev.smartkisan.data.ChatRoleEnum
import com.appdev.smartkisan.ui.theme.lightBlue
import com.appdev.smartkisan.ui.theme.myGreen

@Composable
fun MessageItem(chat: Chat) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, end = 10.dp, start = 10.dp),
        horizontalAlignment = if (chat.role == ChatRoleEnum.USER.value) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(
                    if (chat.role == ChatRoleEnum.USER.value) myGreen else if (isSystemInDarkTheme()) Color(
                        0xFFAFD7DC
                    ) else lightBlue,
                    RoundedCornerShape(
                        topEnd = if (chat.role == ChatRoleEnum.USER.value) 0.dp else 10.dp,
                        topStart = if (chat.role == ChatRoleEnum.USER.value) 10.dp else 0.dp,
                        bottomEnd = 10.dp,
                        bottomStart = 10.dp
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatMessageWithBoldText(chat.message),
                fontSize = 15.sp,
                color = if (chat.role == ChatRoleEnum.USER.value) Color.White else Color.Black.copy(
                    alpha = 0.9f
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }
    }
}

private fun formatMessageWithBoldText(message: String): AnnotatedString {
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