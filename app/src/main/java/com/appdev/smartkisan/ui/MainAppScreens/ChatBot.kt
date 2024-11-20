package com.appdev.smartkisan.ui.MainAppScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appdev.smartkisan.R
import com.appdev.smartkisan.data.SettingOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(controller: NavHostController) {
    var message by remember {
        mutableStateOf("")
    }
    val listOFQuestions by remember {
        mutableStateOf(
            listOf(
                "What is the best fertilizer for wheat crops?",
                "What causes yellow leaves in rice plants?",
                "How to test soil quality for better farming?",
                "What are the symptoms of late blight in potatoes?"
            )
        )
    }
    Scaffold(topBar = {
        TopAppBar(navigationIcon = {
            Card(onClick = {
                controller.navigateUp()
            }, shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(start = 10.dp)) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
        }, title = {

            Text(
                text = "AgriBot",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        })
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
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
                listOFQuestions.forEach { question ->
                    singleQuestion(question) {

                    }
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = message,
                    onValueChange = { input ->
                        message = input
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = if (isSystemInDarkTheme()) Color(0xFF114646) else Color(
                            0xFFE4E7EE
                        ),
                        unfocusedContainerColor = if (isSystemInDarkTheme()) Color(0xFF114646) else Color(
                            0xFFE4E7EE
                        )
                    ),
                    placeholder = {
                        Text(
                            text = "Ask me anything"
                        )
                    },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)
                )
                Card(
                    onClick = {},
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xff68BB59)
                    )
                ) {
                    Box(modifier = Modifier.padding(10.dp)) {
                        Image(
                            painter = painterResource(R.drawable.send),
                            contentDescription = "",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun singleQuestion(question: String, onClick: () -> Unit) {
    Card(
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSystemInDarkTheme()) Color.Gray else Color(0xff1E5631)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) Color(
                0xFF114646
            ) else Color(0xff68BB59)
        ),
        modifier = Modifier.padding(top = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 15.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = question,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White, textAlign = TextAlign.Center
            )
        }
    }
}