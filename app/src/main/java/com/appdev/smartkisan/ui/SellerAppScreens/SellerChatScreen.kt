package com.appdev.smartkisan.ui.SellerAppScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.R
import com.appdev.smartkisan.Utils.Functions.toConvertTime
import com.appdev.smartkisan.data.ChatMateData
import com.appdev.smartkisan.ui.OtherComponents.SearchField
import com.appdev.smartkisan.ui.navigation.Routes
import com.appdev.smartkisan.ui.theme.SmartKisanTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerChatScreen(controller: NavHostController, onSearchFocusChange: (Boolean) -> Unit) {
    var query by remember {
        mutableStateOf("")
    }

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
                SearchField(query = query,
                    placeholder = "Search...",
                    modifier = Modifier
                        .fillMaxWidth(),
                    onFocusChange = { state -> onSearchFocusChange(state) },
                    onTextChange = { text -> query = text }) {
                    focusManager.clearFocus()
                    query = ""
                }
//                TextField(
//                    value = query,
//                    onValueChange = { input ->
//                        query = input
//                    },
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = Color.Transparent,
//                        unfocusedIndicatorColor = Color.Transparent,
//                        focusedContainerColor = if (isSystemInDarkTheme()) Color(0xFF114646) else Color(
//                            0xFFE4E7EE
//                        ),
//                        unfocusedContainerColor = if (isSystemInDarkTheme()) Color(0xFF114646) else Color(
//                            0xFFE4E7EE
//                        )
//                    ),
//                    placeholder = {
//                        Text(
//                            text = "Search..."
//                        )
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .onFocusChanged { focusState ->
//                            onSearchFocusChange(focusState.isFocused)
//                            isFocused = focusState.isFocused
//                        },
//                    singleLine = true, leadingIcon = {
//                        if (isFocused) {
//                            IconButton(onClick = {
//                                focusManager.clearFocus()
//                                query = ""
//                            }) {
//                                Icon(
//                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                                    contentDescription = ""
//                                )
//                            }
//                        }
//                    }, shape = RoundedCornerShape(10.dp)
//                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    itemsIndexed(
                        chatData,
                        key = { _, eachContact -> eachContact.partnerId!! }
                    ) { index, eachContact ->
                        Column {
                            ListRowData(
                                chatMateData = eachContact
                            ) {
                                controller.navigate(Routes.ChatInDetailScreen.route)
                            }
                        }
                    }
                }


            }
        }
    }
}


@Composable
fun ListRowData(chatMateData: ChatMateData, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clickable {
                onClick()
            }) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(chatMateData.receiverImage).build(),
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
            Text(
                text = chatMateData.receiverName,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = chatMateData.lastMessage ?: "",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }
        Text(
            text = toConvertTime(chatMateData.lastMessageTime ?: 0L),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f)
        )
    }
}

fun getDummyChatData(): List<ChatMateData> {
    return listOf(
        ChatMateData(
            receiverName = "Alice",
            partnerId = "1",
            lastMessage = "Hey, how are you? i am under the water",
            lastMessageTime = System.currentTimeMillis() - 60000
        ),
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartKisanTheme {
        SellerChatScreen(controller = rememberNavController()) { }
    }
}
