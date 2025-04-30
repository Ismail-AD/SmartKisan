package com.appdev.smartkisan.ui.MainAppScreens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appdev.smartkisan.R
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.appdev.smartkisan.Actions.ChatBotScreenActions
import com.appdev.smartkisan.States.ChatBotUiState
import com.appdev.smartkisan.ViewModel.ChatBotViewModel
import com.appdev.smartkisan.ui.OtherComponents.MessageItem

@Composable
fun ChatBotRoot(
    controller: NavHostController,
    chatBotViewModel: ChatBotViewModel = hiltViewModel()
) {
    ChatBotScreen(chatBotViewModel.chatBotUiState) { action ->
        when (action) {
            is ChatBotScreenActions.GoBack -> {
                controller.navigateUp()
            }

            else -> chatBotViewModel.onAction(action)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(uiState: ChatBotUiState, onAction: (ChatBotScreenActions) -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            onAction(ChatBotScreenActions.AddSelectedImages(uris))
        }
    }

    Scaffold(topBar = {
        TopAppBar(navigationIcon = {
            Card(onClick = {
                onAction(ChatBotScreenActions.GoBack)
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
    }, bottomBar = {
        Column(
            modifier = Modifier
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
                    items(
                        uiState.selectedImageUris,
                        key = { uri -> uri.toString() }
                    ) { imageUri ->
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
                                            onAction(
                                                ChatBotScreenActions.RemoveImage(
                                                    imageUri
                                                )
                                            )
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
                        if (isSystemInDarkTheme()) Color(0xFF114646) else Color(
                            0xFFE4E7EE
                        )
                    )
            ) {
                // This is the key structure change - Box with elements positioned at bottom
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = uiState.userMessage,
                        onValueChange = { input ->
                            onAction(ChatBotScreenActions.SetMessage(input))
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                        placeholder = {
                            Text(
                                text = "Ask me anything", fontSize = 15.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                            .heightIn(min = 56.dp, max = 170.dp), // Padding to make space for icons
                        shape = RoundedCornerShape(0.dp), enabled = !uiState.isLoading
                    )

                    // Add image button - aligned to bottom start
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 8.dp, bottom = 8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                multiplePhotoPickerLauncher.launch("image/*")
                            },
                            modifier = Modifier.size(40.dp), enabled = !uiState.isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = "Add Photo",
                                tint = Color(0xff68BB59),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // Send button - aligned to bottom end
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 8.dp, bottom = 8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onAction.invoke(ChatBotScreenActions.SendPrompt(uiState.userMessage))
                            },
                            modifier = Modifier
                                .background(
                                    color = Color(0xff68BB59),
                                    shape = CircleShape
                                )
                                .size(36.dp), enabled = !uiState.isLoading
                        ) {
                            when (uiState.isLoading) {
                                true -> {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                else -> {
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
            }
        }
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (uiState.listOfMessages.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
                ) {
                    items(
                        items = uiState.listOfMessages,
                        key = { eachMessage -> eachMessage.timeStamp ?: 0L }
                    ) { eachMessage ->
                        MessageItem(eachMessage)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()), // Make content scrollable,
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
                    uiState.suggestedQuestions.forEach { question ->
                        singleQuestion(question) {
                            onAction.invoke(ChatBotScreenActions.SendPrompt(question))
                        }
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