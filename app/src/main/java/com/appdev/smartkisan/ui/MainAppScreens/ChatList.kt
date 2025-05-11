package com.appdev.smartkisan.ui.MainAppScreens

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.Actions.ChatListActions
import com.appdev.smartkisan.Actions.HomeScreenActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.ChatListUiState
import com.appdev.smartkisan.ViewModel.RecentChatsViewModel
import com.appdev.smartkisan.data.ChatMateData
import com.appdev.smartkisan.data.UserEntity
import com.appdev.smartkisan.ui.SellerAppScreens.ListRowData
import com.appdev.smartkisan.ui.navigation.Routes
import com.appdev.smartkisan.ui.theme.myGreen

@Composable
fun ChatListRoot(
    controller: NavHostController,
    recentChatsViewModel: RecentChatsViewModel = hiltViewModel()
) {
    val state by recentChatsViewModel.state.collectAsStateWithLifecycle()
    ChatListScreen(state) { action ->
        when (action) {
            is ChatListActions.MessageAUser -> {
                val encodedProfilePic = Uri.encode(action.profilePic)
                controller.navigate(Routes.ChatInDetailScreen.route + "/${action.receiverId}/${action.name}/${encodedProfilePic}")
            }

            else -> recentChatsViewModel.onAction(action)
        }
    }
}

@Composable
fun ChatListScreen(
    state: ChatListUiState,
    onAction: (ChatListActions) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ClickableTabs(
            selectedItem = state.currentTab,
            tabsList = listOf("Messages", "Chat With"),
            onClick = { tabIndex ->
                onAction(ChatListActions.CurrentSelectedTab(tabIndex))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = myGreen)
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error,
                        color = Color.Red,
                        style = typography.bodyMedium
                    )
                }
            }

            else -> {
                // Content based on selected tab
                when (state.currentTab) {
                    0 -> {
                        when {
                            state.recentMessages.isEmpty() -> EmptyState(message = "No messages found")
                            else -> MessagesList(messages = state.recentMessages)
                        }
                    }

                    1 -> {
                        when {
                            state.chatWithList.isEmpty() -> EmptyState(message = "No sellers found")
                            else -> ChatWithList(chatWithList = state.chatWithList) { id, name, image ->
                                onAction.invoke(ChatListActions.MessageAUser(id, name, image))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MessagesList(messages: List<ChatMateData>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            messages,
            key = { _, eachContact -> eachContact.partnerId!! }
        ) { index, eachContact ->
            Column {
                ListRowData(
                    chatMateData = eachContact
                ) {

                }
            }
        }
    }
}


@Composable
fun ChatWithList(chatWithList: List<UserEntity>, messageUser: (String, String, String?) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(chatWithList) { seller ->
            SellerItem(seller = seller) {
                if (seller.id != null) {
                    messageUser(seller.id!!, seller.name, seller.imageUrl)
                }
            }
        }
    }
}

@Composable
fun SellerItem(seller: UserEntity, messageUser: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .clickable {
                    messageUser()
                }) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(null).build(),
                error = painterResource(R.drawable.farmer),
                placeholder = painterResource(R.drawable.farmer),
                contentDescription = "pImage",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .padding(10.dp)
                    .clip(CircleShape)
            )
            Text(
                text = seller.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

    }
}

@Composable
fun ClickableTabs(selectedItem: Int, tabsList: List<String>, onClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(
                border = BorderStroke(
                    1.dp,
                    myGreen
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabsList.forEachIndexed { index, s ->
                TabItem(
                    isSelected = index == selectedItem,
                    text = s,
                    Modifier.weight(0.5f)
                ) {
                    onClick.invoke(index)
                }
            }
        }
    }
}

@Composable
fun TabItem(isSelected: Boolean, text: String, modifier: Modifier, onClick: () -> Unit) {
    val tabTextColor: Color by animateColorAsState(
        targetValue = when (isSelected) {
            true -> Color.White
            false -> MaterialTheme.colorScheme.onBackground
        },
        animationSpec = tween(easing = LinearEasing),
        label = ""
    )

    val background: Color by animateColorAsState(
        targetValue = when (isSelected) {
            true -> myGreen
            false -> Color.White
        },
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = ""
    )

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)
            .background(background, RoundedCornerShape(5.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick.invoke()
            }
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = typography.titleSmall,
            textAlign = TextAlign.Center,
            color = tabTextColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TabsPreview() {
    Column(modifier = Modifier.padding(20.dp)) {
        ClickableTabs(selectedItem = 1, tabsList = listOf("Messages", "Chat With"), onClick = {})
    }
}