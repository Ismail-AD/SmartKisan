package com.appdev.smartkisan.ui.SellerAppScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.Greeting
import com.appdev.smartkisan.R
import com.appdev.smartkisan.Utils.Functions.toConvertTime
import com.appdev.smartkisan.data.ChatMateData
import com.appdev.smartkisan.ui.theme.SmartKisanTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerChatScreen(controller: NavHostController, onSearchFocusChange: (Boolean) -> Unit) {
    var query by remember {
        mutableStateOf("")
    }
    var isFocused by remember { mutableStateOf(false) }
    val chatData = remember { getDummyChatData() }
    val focusManager = LocalFocusManager.current
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Messages",
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
                    .padding(horizontal = 20.dp)
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
                            text = "Search..."
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            onSearchFocusChange(focusState.isFocused)
                            isFocused = focusState.isFocused
                        },
                    singleLine = true,leadingIcon = {
                        if(isFocused){
                            IconButton(onClick = {
                                focusManager.clearFocus()
                                query = ""
                            }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                            }
                        }
                    }, shape = RoundedCornerShape(10.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 10.dp)
                ) {
                    itemsIndexed(
                        chatData,
                        key = { _, eachContact -> eachContact.userid!! }
                    ) { index, eachContact ->
                        Column {
                            ListRowData(
                                chatMateData = eachContact
                            )
                        }
                    }
                }


            }
        }
    }
}


@Composable
fun ListRowData(chatMateData: ChatMateData) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clickable {

            }) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(null).build(),
            error = painterResource(R.drawable.farmer),
            placeholder = painterResource(R.drawable.farmer),
            contentDescription = "pImage",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(75.dp)
                .padding(10.dp)
                .clip(CircleShape)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            chatMateData.username?.let {
                Text(
                    text = it,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
            chatMateData.lastMsg?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (chatMateData.chatTime!! > 0) {
            Text(
                text = toConvertTime(chatMateData.chatTime),
                fontSize = 13.sp,
                color = Color.Gray, modifier = Modifier.weight(1f)
            )
        }
    }
}

fun getDummyChatData(): List<ChatMateData> {
    return listOf(
        ChatMateData(
            username = "Alice",
            userid = "1",
            userContact = "+123456789",
            lastMsg = "Hey, how are you? i am under the water",
            chatTime = System.currentTimeMillis() - 60000
        ),
        ChatMateData(
            username = "Bob",
            userid = "2",
            userContact = "+987654321",
            lastMsg = "See you tomorrow.",
            chatTime = System.currentTimeMillis() - 120000
        ),
        ChatMateData(
            username = "Charlie",
            userid = "3",
            userContact = "+192837465",
            lastMsg = "Let's catch up later!",
            chatTime = System.currentTimeMillis() - 3600000
        ),
        ChatMateData(
            username = "Diana",
            userid = "4",
            userContact = "+1122334455",
            lastMsg = "Meeting at 5 PM?",
            chatTime = System.currentTimeMillis() - 7200000
        ),
        ChatMateData(
            username = "Eve",
            userid = "5",
            userContact = "+5566778899",
            lastMsg = "Got your message.",
            chatTime = System.currentTimeMillis() - 14400000
        ),
        ChatMateData(
            username = "Frank",
            userid = "6",
            userContact = "+9988776655",
            lastMsg = "Thanks for the update!",
            chatTime = System.currentTimeMillis() - 28800000
        ),
        ChatMateData(
            username = "Grace",
            userid = "7",
            userContact = "+5544332211",
            lastMsg = "Can you call me?",
            chatTime = System.currentTimeMillis() - 86400000
        ),
        ChatMateData(
            username = "Henry",
            userid = "8",
            userContact = "+6677889900",
            lastMsg = "Check your email.",
            chatTime = System.currentTimeMillis() - 172800000
        ),
        ChatMateData(
            username = "Ivy",
            userid = "9",
            userContact = "+2233445566",
            lastMsg = "Happy birthday!",
            chatTime = System.currentTimeMillis() - 259200000
        ),
        ChatMateData(
            username = "Jake",
            userid = "10",
            userContact = "+3344556677",
            lastMsg = "Good night!",
            chatTime = System.currentTimeMillis() - 345600000
        )
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartKisanTheme {
        SellerChatScreen(controller = rememberNavController()) { }
    }
}
