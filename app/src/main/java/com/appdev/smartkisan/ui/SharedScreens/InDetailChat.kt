package com.appdev.smartkisan.ui.SharedScreens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.appdev.smartkisan.Actions.ChatActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.ChatUiState
import com.appdev.smartkisan.Utils.Functions
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.ViewModel.ChatMessagesViewModel
import com.appdev.smartkisan.data.ChatMessage
import com.appdev.smartkisan.ui.theme.lightBlue
import com.appdev.smartkisan.ui.theme.myGreen


@Composable
fun ChatMessagesRoot(
    controller: NavHostController,
    chatMessagesViewModel: ChatMessagesViewModel = hiltViewModel()
) {
    val state by chatMessagesViewModel.state.collectAsStateWithLifecycle()

    InDetailChatScreen(state, chatMessagesViewModel.currentUserId) { action ->
        when (action) {
            ChatActions.GoBack -> {
                controller.navigateUp()
            }

            else -> chatMessagesViewModel.onAction(action)
        }
    }
}


/**
 * Enhanced version of the InDetailChatScreen that handles different states
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InDetailChatScreen(state: ChatUiState, currentUserId: String, onAction: (ChatActions) -> Unit) {
    val listState = rememberLazyListState()
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            onAction(ChatActions.AddSelectedImages(uris))
        }
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(topBar = {
        ChatTopBar(name = state.receiverName, profilePic = state.receiverProfilePic) {
            onAction.invoke(ChatActions.GoBack)
        }
    }, bottomBar = {
        ChatInputField(
            state,
            onAction = onAction,
            multiplePhotoPickerLauncher,
        )
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when {
                // Show loading state
                state.isLoading && state.messages.isEmpty() -> {
                    LoadingChatState(modifier = Modifier.align(Alignment.Center))
                }
                // Show empty state when no messages
                state.messages.isEmpty() -> {
                    EmptyChatState(modifier = Modifier.align(Alignment.Center))
                }
                // Show messages
                else -> {
                    ChatMessagesList(
                        messages = state.messages,
                        currentUserId = currentUserId,
                        listState = listState,
                        state.receiverId
                    )
                }
            }
        }
    }
}

/**
 * Loading state with centered circular progress
 */
@Composable
fun LoadingChatState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xff68BB59),
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "Loading conversation...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/**
 * Empty state when no messages are found
 */
@Composable
fun EmptyChatState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.send),
            contentDescription = "No messages",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp),
            alpha = 0.6f
        )
        Text(
            text = "No messages yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Start the conversation by sending a message below",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )
    }
}

/**
 * List of chat messages
 */
@Composable
fun ChatMessagesList(
    messages: List<ChatMessage>,
    currentUserId: String,
    listState: LazyListState,
    receiverId: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        state = listState,
        contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
    ) {
        items(
            items = messages,
            key = { message -> message.timeStamp ?: 0L }
        ) { message ->
            MessageItem(message, currentUserId, message.senderID != receiverId)
        }
    }
}

/**
 * Individual message item with different types
 */
@Composable
fun MessageItem(message: ChatMessage, currentUserId: String, isCurrentUser: Boolean) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        when {
            // Image message
//            !message.imageUrl.isNullOrEmpty() -> {
//                ImageMessageBubble(
//                    message = message,
//                    isCurrentUser = isCurrentUser,
//                    backgroundColor = backgroundColor
//                )
//            }

            // Text message
            !message.message.isNullOrEmpty() -> {
                TextMessageBubble(
                    message = message,
                    isCurrentUser = isCurrentUser,
                    backgroundColor = if (isCurrentUser)
                        myGreen
                    else if (isSystemInDarkTheme())
                        Color(0xFFAFD7DC)
                    else
                        lightBlue,
                    textColor = if (isCurrentUser)
                        Color.White
                    else
                        Color.Black.copy(alpha = 0.9f)
                )
            }

        }

        // Timestamp for all message types
        Text(
            text = Functions.toConvertTime(message.timeStamp),
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            textAlign = if (isCurrentUser) TextAlign.End else TextAlign.Start
        )
    }
}

/**
 * Text message bubble
 */
@Composable
fun TextMessageBubble(
    message: ChatMessage,
    isCurrentUser: Boolean,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (isCurrentUser) 10.dp else 0.dp,
                    topEnd = if (isCurrentUser) 0.dp else 10.dp,
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            )
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = message.message ?: "",
            style = TextStyle(
                fontSize = 15.sp,
                color = textColor
            )
        )
    }
}

/**
 * Image message bubble
 */
@Composable
fun ImageMessageBubble(
    message: ChatMessage,
    isCurrentUser: Boolean,
    backgroundColor: Color
) {
    Card(
        shape = RoundedCornerShape(
            topStart = if (isCurrentUser) 10.dp else 0.dp,
            topEnd = if (isCurrentUser) 0.dp else 10.dp,
            bottomStart = 10.dp,
            bottomEnd = 10.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Image
//            AsyncImage(
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(message.imageUrl)
//                    .crossfade(true)
//                    .build(),
//                contentDescription = "Message Image",
//                contentScale = ContentScale.FillWidth,
//                modifier = Modifier
//                    .size(200.dp)
//                    .clip(RoundedCornerShape(8.dp))
//            )

            // Optional text with image
            if (!message.message.isNullOrEmpty()) {
                Text(
                    text = message.message,
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = if (isCurrentUser) Color.White else Color.Black.copy(alpha = 0.9f)
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

/**
 * Loading message bubble (typing indicator)
 */
@Composable
fun LoadingMessageBubble(
    isCurrentUser: Boolean,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (isCurrentUser) 10.dp else 0.dp,
                    topEnd = if (isCurrentUser) 0.dp else 10.dp,
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            )
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Typing indicator dots
            repeat(3) {
                LoadingDot(delay = it * 300)
            }
        }
    }
}

/**
 * Animated loading dot for typing indicator
 */
@Composable
fun LoadingDot(delay: Int) {
    var animateStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        animateStarted = true
    }

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = if (animateStarted) 0.9f else 0.4f))
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    name: String,
    profilePic: String? = null,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profilePic)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.farmer),
                    error = painterResource(id = R.drawable.farmer),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(23.dp)
                )
            }
        },
    )
}

@Composable
fun ChatInputField(
    uiState: ChatUiState,
    onAction: (ChatActions) -> Unit,
    multiplePhotoPickerLauncher: ActivityResultLauncher<String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        // Images list row - appears above the input field when images exist
        AnimatedVisibility(visible = uiState.selectedImageUris.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.selectedImageUris, key = { uri -> uri.toString() }) { imageUri ->
                    Box(
                        modifier = Modifier.size(70.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "",
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .align(Alignment.TopEnd)
                                .clickable {
                                    if (!uiState.isLoading) {
                                        onAction(ChatActions.RemoveImage(imageUri))
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Remove Image",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Input container with background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isSystemInDarkTheme()) Color(0xFF114646) else Color(0xFFE4E7EE)
                )
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Text field that can grow upward
                TextField(
                    value = uiState.messageInput,
                    onValueChange = { input -> onAction(ChatActions.UpdateMessageInput(input)) },
                    textStyle = TextStyle(fontSize = 15.sp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    placeholder = { Text(text = "Ask me anything", fontSize = 15.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .heightIn(min = 56.dp, max = 170.dp),
                    shape = RoundedCornerShape(0.dp),
                    enabled = !uiState.isLoading
                )

                // Add image button - aligned to bottom start
                IconButton(
                    onClick = { multiplePhotoPickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 8.dp)
                        .size(40.dp),
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = "Add Photo",
                        tint = Color(0xff68BB59),
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Send button - aligned to bottom end
                IconButton(
                    onClick = {
                        onAction(
                            ChatActions.SendMessage(
                                uiState.messageInput,
                                myImage = uiState.userImage,
                                myName = uiState.userName
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp)
                        .background(color = Color(0xff68BB59), shape = CircleShape)
                        .size(36.dp),
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        painter = painterResource(R.drawable.send),
                        contentDescription = "Send message",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

            }
        }
    }
}
