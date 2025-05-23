package com.appdev.smartkisan.presentation.feature.Seller.inbox

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.presentation.feature.farmer.chats.ChatListActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.presentation.feature.farmer.chats.ChatListUiState
import com.appdev.smartkisan.Utils.Functions.toConvertTime
import com.appdev.smartkisan.domain.model.ChatMateData
import com.appdev.smartkisan.presentation.feature.farmer.chats.RecentChatsViewModel
import com.appdev.smartkisan.presentation.Resuable.NoDialogLoader
import com.appdev.smartkisan.presentation.Resuable.SearchField
import com.appdev.smartkisan.presentation.navigation.Routes


@Composable
fun SellerChatScreenRoot(
    controller: NavHostController,
    recentChatsViewModel: RecentChatsViewModel = hiltViewModel()
) {
    val state by recentChatsViewModel.state.collectAsStateWithLifecycle()

    SellerChatScreen (state) { action ->
        when (action) {
            is ChatListActions.MessageAUser -> {
                val encodedProfilePic = Uri.encode(action.profilePic)
                controller.navigate(Routes.ChatInDetailScreen.route + "/${action.receiverId}/${action.name}/${encodedProfilePic}")
            }

            else -> recentChatsViewModel.onAction(action)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerChatScreen(
    state: ChatListUiState,
    onAction: (ChatListActions) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Messages",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        },colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
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
                SearchField(query = state.query,
                    placeholder = "Search...",
                    modifier = Modifier
                        .fillMaxWidth(),
                    onFocusChange = { /* Handle focus change if needed */ },
                    onTextChange = { text ->
                        onAction(ChatListActions.UpdateQuery(text))

                        onAction(ChatListActions.SearchChats(text))
                    }) {
                    focusManager.clearFocus()
                    onAction(ChatListActions.SearchChats(""))
                    onAction(ChatListActions.UpdateQuery(""))
                }

                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        NoDialogLoader("Loading Messages...")
                    }
                } else if (state.error != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.error ?: "An error occurred")
                    }
                } else if (state.recentMessages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No messages yet")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        itemsIndexed(
                            state.filteredChats.ifEmpty { state.recentMessages },
                            key = { _, eachContact -> eachContact.partnerId ?: "" }
                        ) { _, eachContact ->
                            Column {
                                ListRowData(
                                    chatMateData = eachContact
                                ) {
                                    Log.d("CAZ","${eachContact}")
                                    onAction(
                                        ChatListActions.MessageAUser(
                                        receiverId = eachContact.partnerId ?: "",
                                        name = eachContact.receiverName ?:"",
                                        profilePic = eachContact.receiverImage ?: ""
                                    ))
                                }
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
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp, bottom = 3.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 2.dp, end = 10.dp)
        ) {
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
                    text = chatMateData.receiverName ?: "",
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

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = toConvertTime(chatMateData.lastMessageTime ?: 0L),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Show unread count if any
                if ((chatMateData.unreadCount ?: 0) > 0) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    ) {
                        Text(
                            text = chatMateData.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}