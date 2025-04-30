package com.appdev.smartkisan.ui.MainAppScreens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.appdev.smartkisan.Actions.HomeScreenActions
import com.appdev.smartkisan.Actions.MarketplaceActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.ViewModel.HomeScreenViewModel
import com.appdev.smartkisan.ViewModel.MarketplaceViewModel
import com.appdev.smartkisan.ui.navigation.Routes
import com.appdev.smartkisan.ui.theme.myGreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Composable
fun HomeRoot(
    controller: NavHostController,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    HomeScreen { action ->
        when (action) {
            is HomeScreenActions.GoToChatBotScreen -> {
                controller.navigate(Routes.ChatBotScreen.route)
            }

            else -> homeScreenViewModel.onAction(action)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onAction: (HomeScreenActions) -> Unit) {
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(title = {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Hello ${SessionManagement.getUserName(context)} \uD83D\uDC4B",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
//                Image(
//                    painter = painterResource(id = R.drawable.farmer),
//                    contentDescription = "",
//                    modifier = Modifier.size(28.dp)
//                )
            }
        }, actions = {
//            IconButton(onClick = {
//                controller.navigate(Routes.ChatBotScreen.route)
//            }) {
            Box(modifier = Modifier.padding(end = 12.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(SessionManagement.getUserImage(context))
                        .crossfade(true)
                        .build(),
                    contentDescription = "User Image",
                    placeholder = painterResource(R.drawable.farmer),
                    error = painterResource(R.drawable.farmer),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop  // Changed from FillBounds to Crop for better circle fitting
                )
            }

//            }
        }, modifier = Modifier.shadow(1.dp))
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
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                WeatherCard()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) Color(
                            0xFF114646
                        ) else myGreen
                    ),
                    onClick = {
                        onAction(HomeScreenActions.GoToChatBotScreen)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.chatbot),
                            contentDescription = "ChatBot",
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Ask Kisan Assistant",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

            }
        }
    }

}

@Composable
fun WeatherCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        colors = CardDefaults.cardColors(containerColor = colorCalculation())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row() {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.weight(1f)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(
                            text = "21\t\u00B0",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColorCalculation()
                        )
                        Text(
                            text = "Mostly Sunny",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColorCalculation().copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Sargodha, Pakistan",
                            fontSize = 15.sp,
                            color = textColorCalculation()
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.day),
                            contentDescription = "",
                            modifier = Modifier.size(110.dp)
                        )
                    }
                }
            }
        }
    }
}

fun colorCalculation(): Color {
    return Color(0xB7FFE484)
}

fun textColorCalculation(): Color {
    return Color.Black
}