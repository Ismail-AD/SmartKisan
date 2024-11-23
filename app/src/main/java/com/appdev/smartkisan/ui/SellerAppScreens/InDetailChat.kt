package com.appdev.smartkisan.ui.SellerAppScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appdev.smartkisan.R
import com.appdev.smartkisan.Utils.Functions
import com.appdev.smartkisan.data.ChatMessage
import com.appdev.smartkisan.ui.theme.lightBlue
import com.appdev.smartkisan.ui.theme.myGreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InDetailChatScreen(controller: NavHostController) {
    var query by remember {
        mutableStateOf("")
    }
    val fakeMessages = listOf(
        ChatMessage(
            message = "Hey, how are you?",
            userID = "user_1",
            timeStamp = System.currentTimeMillis() - 60_000
        ),
        ChatMessage(
            message = "I'm good, what about you? WindowInsets.ime.getBottom: Used to check the IME bottom inset in pixels, which indicates whether the keyboard is open.\n" +
                    "LocalView and LocalDensity: These help to determine the current state of the view and translate insets into density-aware pixels.",
            userID = "user_2",
            timeStamp = System.currentTimeMillis() - 50_000
        ),
        ChatMessage(
            message = "Doing great, thanks for asking!",
            userID = "user_1",
            timeStamp = System.currentTimeMillis() - 40_000
        ),
        ChatMessage(
            message = "Are we still meeting later?",
            userID = "user_2",
            timeStamp = System.currentTimeMillis() - 30_000
        ),
        ChatMessage(
            message = "Yes, let's meet at 6 PM.",
            userID = "user_1",
            timeStamp = System.currentTimeMillis() - 20_000
        ),
        ChatMessage(
            message = "Perfect, see you then!",
            userID = "user_2",
            timeStamp = System.currentTimeMillis() - 10_000
        )
    )

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Saad",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onBackground
            )
        }, navigationIcon = {
            IconButton(onClick = {

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(23.dp)
                )
            }
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
                    .padding(horizontal = 10.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Use weight to make it scrollable
                    contentPadding = PaddingValues(top = 10.dp, bottom = 70.dp)
                ) {
                    items(
                        items = fakeMessages,
                        key = { eachMessage -> eachMessage.timeStamp ?: 0L }
                    ) { eachMessage ->
                        EachMessage("user_1", eachMessage)
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
                    value = query,
                    onValueChange = { input ->
                        query = input
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
                            text = "Input message here..."
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
fun EachMessage(senderID: String, eachMessage: ChatMessage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, end = 10.dp, start = 10.dp),
        horizontalAlignment = if (eachMessage.userID.equals(senderID)) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (eachMessage.userID.equals(senderID)) myGreen else if (isSystemInDarkTheme()) Color(
                        0xFFAFD7DC
                    ) else lightBlue,
                    RoundedCornerShape(
                        topEnd = if (eachMessage.userID.equals(senderID)) 0.dp else 10.dp,
                        topStart = if (eachMessage.userID.equals(senderID)) 10.dp else 0.dp,
                        bottomEnd = 10.dp,
                        bottomStart = 10.dp
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            eachMessage.message?.let {
                Text(
                    text = it,
                    fontSize = 15.sp,
                    color = if (eachMessage.userID.equals(senderID)) Color.White else Color.Black.copy(
                        alpha = 0.9f
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                )
            }
        }
        Text(
            text = Functions.toConvertTime(eachMessage.timeStamp),
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}