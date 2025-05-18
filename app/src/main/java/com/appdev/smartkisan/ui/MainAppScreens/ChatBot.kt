package com.appdev.smartkisan.ui.MainAppScreens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.appdev.smartkisan.Actions.ChatBotScreenActions
import com.appdev.smartkisan.Actions.DiseaseDetectActions
import com.appdev.smartkisan.States.ChatBotUiState
import com.appdev.smartkisan.ViewModel.ChatBotViewModel
import com.appdev.smartkisan.data.BotChatMessage
import com.appdev.smartkisan.ui.OtherComponents.AudioWaveRecordingView
import com.appdev.smartkisan.ui.OtherComponents.AudioWaveVisualization
import com.appdev.smartkisan.ui.OtherComponents.BotMessageItem
import com.appdev.smartkisan.ui.OtherComponents.MessageItem
import com.appdev.smartkisan.ui.OtherComponents.NoDialogLoader
import com.appdev.smartkisan.ui.OtherComponents.SpeechRecordingButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ChatBotScreen(uiState: ChatBotUiState, onAction: (ChatBotScreenActions) -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
    val context = LocalContext.current

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            onAction(ChatBotScreenActions.AddSelectedImages(uris))
        }
    }

    // LazyListState for scrolling control
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll logic when messages are updated
    LaunchedEffect(key1 = uiState.shouldScrollToBottom, key2 = uiState.listOfMessages.size) {
        if (uiState.shouldScrollToBottom && uiState.listOfMessages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.listOfMessages.size)
            }
        }
    }

    // Date picker dialog
    if (uiState.isDatePickerDialogVisible) {
        DatePickerDialog(
            availableDates = uiState.formattedAvailableDates,
            currentDate = uiState.currentDate,
            onDismiss = { onAction(ChatBotScreenActions.HideDatePickerDialog) },
            onDateSelected = { date ->
                onAction(ChatBotScreenActions.LoadMessagesByDate(date))
                onAction(ChatBotScreenActions.HideDatePickerDialog)
            },
            onReturnToToday = {
                onAction(ChatBotScreenActions.ReturnToToday)
                onAction(ChatBotScreenActions.HideDatePickerDialog)
            }
        )
    }

    if (uiState.showRecordingDialog) {
        SpeechRecordingDialog(
            amplitude = uiState.speechAmplitude,
            isRecording = uiState.isListening,
            onClose = {
                // User manually closed the dialog - discard any partial text
                onAction(ChatBotScreenActions.CloseRecordingDialog)
            },
            onFinishRecording = {
                // User tapped to stop recording - save the recognized text
                onAction(ChatBotScreenActions.StopSpeechRecognition)
            }
        )
    }

    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                Card(
                    onClick = { onAction(ChatBotScreenActions.GoBack) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "AgriBot",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                // History button with indicator if in history mode
                IconButton(
                    onClick = { onAction(ChatBotScreenActions.ShowDatePickerDialog) }
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = "Chat History",
                            tint = if (uiState.isHistoryMode) Color(0xff68BB59) else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
    }, bottomBar = {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                // Display a banner when viewing history
                AnimatedVisibility(visible = uiState.isHistoryMode) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE4F9DD))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Viewing chat history",
                                    color = Color(0xff1E5631),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = uiState.formattedAvailableDates[uiState.currentDate] ?: uiState.currentDate,
                                    color = Color(0xff1E5631),
                                    fontSize = 12.sp
                                )
                            }

                            IconButton(
                                onClick = { onAction(ChatBotScreenActions.ReturnToToday) },
                                modifier = Modifier
                                    .background(Color(0xff68BB59), CircleShape)
                                    .size(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow), // Replace with appropriate icon
                                    contentDescription = "Return to Today",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Only show input field and image picker when NOT in history mode
                AnimatedVisibility(visible = !uiState.isHistoryMode) {
                    Column {
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
                                                    if (!uiState.isSendingMessage) {
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
                        ) {
                            // This is the key structure change - Box with elements positioned at bottom
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left side controls row
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Add image button
                                    IconButton(
                                        onClick = {
                                            multiplePhotoPickerLauncher.launch("image/*")
                                        },
                                        modifier = Modifier.size(40.dp),
                                        enabled = !uiState.isSendingMessage && !uiState.isListening
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AddPhotoAlternate,
                                            contentDescription = "Add Photo",
                                            tint = Color(0xff68BB59),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    // Speech recording button
                                    SpeechRecordingButton(
                                        onClick = {
                                            if (permissionState.status.isGranted) {
                                                if (uiState.isListening) {
                                                    onAction(ChatBotScreenActions.StopSpeechRecognition)
                                                } else {
                                                    onAction(ChatBotScreenActions.StartSpeechRecognition)
                                                }
                                            } else if (!permissionState.status.isGranted &&
                                                !permissionState.status.shouldShowRationale
                                            ) {
                                                onAction(ChatBotScreenActions.PermissionDeniedPermanent)
                                            } else {
                                                permissionState.launchPermissionRequest()
                                            }
                                        }
                                    )
                                }
                                TextField(
                                    value = uiState.userMessage,
                                    onValueChange = { input ->
                                        onAction(ChatBotScreenActions.SetMessage(input))
                                    },
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedContainerColor = if (isSystemInDarkTheme()) Color(0xFF114646) else Color(0xFFE4E7EE),
                                        unfocusedContainerColor = if (isSystemInDarkTheme()) Color(0xFF114646) else Color(0xFFE4E7EE),
                                        disabledIndicatorColor = Color.Transparent,
                                        disabledContainerColor = if (isSystemInDarkTheme()) Color(0xFF114646) else Color(0xFFE4E7EE),
                                    ),
                                    placeholder = {
                                        Text(
                                            text = "Ask me anything", fontSize = 16.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 40.dp, max = 170.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = !uiState.isSendingMessage && !uiState.isListening // Disable when recording or sending
                                )

                                // Send button - aligned to bottom end
                                Box(
                                    modifier = Modifier
                                        .padding(start = 4.dp, bottom = 4.dp)
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
                                            .size(34.dp),
                                        enabled = !uiState.isSendingMessage && !uiState.isListening &&
                                                (uiState.userMessage.isNotBlank() || uiState.selectedImageUris.isNotEmpty())
                                    ) {
                                        when (uiState.isSendingMessage) {
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
            if (uiState.isLoadingInitialData) {
                // Show loading indicator for initial data loading
                NoDialogLoader("Loading Chat...")
            } else if (uiState.listOfMessages.isNotEmpty()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
                ) {
                    items(
                        items = uiState.listOfMessages,
                        key = { eachMessage -> eachMessage.id ?: 0L }
                    ) { eachMessage ->
                        BotMessageItem(eachMessage)
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
            if (uiState.showAudioPermitRationale) {
                AlertDialog(
                    onDismissRequest = {
                        onAction(ChatBotScreenActions.DismissMicrophoneDialog)
                    },
                    title = {
                        Text(text = "Microphone Permission Needed")
                    },
                    text = {
                        Text(
                            text = "To use voice input with AgriBot, this app needs access to your device's microphone. " +
                                    "Please enable microphone permission in the app settings to continue."
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onAction(ChatBotScreenActions.DismissMicrophoneDialog)
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                context.startActivity(intent)
                            },
                        ) {
                            Text("Continue")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                onAction(ChatBotScreenActions.DismissMicrophoneDialog)
                            },
                        ) {
                            Text("Dismiss")
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    availableDates: Map<String, String>,
    currentDate: String,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit,
    onReturnToToday: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Chat History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Today chip
                DateChip(
                    label = "Today",
                    isSelected = currentDate == todayFormatted()
                ) {
                    onReturnToToday()
                }

                Spacer(Modifier.height(8.dp))

                // Horizontal list of past dates
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(
                        items = availableDates.entries
                            .sortedByDescending { it.key }
                            .filter { it.key != todayFormatted() }
                    ) { (dateKey, formatted) ->
                        DateChip(
                            label = formatted,
                            isSelected = currentDate == dateKey
                        ) {
                            onDateSelected(dateKey)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun DateChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        tonalElevation = if (isSelected) 4.dp else 0.dp,
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .height(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun SpeechRecordingDialog(
    amplitude: Float,
    isRecording: Boolean,
    onClose: () -> Unit,
    onFinishRecording: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRecording) "Listening..." else "Processing...",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Audio wave visualization
                AudioWaveVisualization(
                    amplitude = amplitude,
                    isRecording = isRecording,
                    primaryColor = Color(0xff68BB59),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Microphone button that allows manual stopping
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (isRecording) Color(0xff68BB59) else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable(onClick = onFinishRecording),  // Make it clickable to stop recording manually
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Recording",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isRecording)
                        "Tap mic to stop or wait for automatic pause detection"
                    else
                        "Processing speech...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


// Helper function to get today's date formatted
private fun todayFormatted(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
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