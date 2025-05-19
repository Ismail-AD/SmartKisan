package com.appdev.smartkisan.presentation.Resuable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.appdev.smartkisan.R
import com.appdev.smartkisan.presentation.feature.farmer.chatbot.singleQuestion

@Composable
fun EmptyState(suggestedQuestions: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.robot_),
            contentDescription = "",
            modifier = Modifier.size(130.dp)
        )
        Text(
            text = "AgriBot",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Not sure what to ask? Here are some ideas to get you started.",
            fontSize = 15.sp,
            modifier = Modifier
                .padding(vertical = 14.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp
        )
        suggestedQuestions.forEach { question ->
            singleQuestion(question) { }
        }
    }
}
