package com.appdev.smartkisan.ui.MainAppScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.Actions.AccountActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.data.SettingOption
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.appdev.smartkisan.ui.navigation.Routes


@Composable
fun AccountRoot(
    controller: NavHostController,
) {
    Account { action ->
        when (action) {
            is AccountActions.GoToChats -> {
                controller.navigate(Routes.UserChatListScreen.route)
            }

            else -> {

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Account(onAction: (AccountActions) -> Unit) {
    val listOfOptions by remember {
        mutableStateOf(
            listOf(
                SettingOption("Edit Profile", R.drawable.edit_text_new_, onClick = {

                }),
                SettingOption("Saved Products", R.drawable.save_, onClick = {

                }),
                SettingOption("Chat", R.drawable.chat, onClick = {
                    onAction.invoke(AccountActions.GoToChats)
                }),
            )
        )
    }
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Account",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
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

                Box(
                    modifier = Modifier
                        .height(220.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                            .background(Color(0xFF2E7D32), RoundedCornerShape(15.dp))
                            .align(Alignment.BottomCenter)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 45.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ali Asif",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "+9245345435",
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .align(Alignment.Center)
                            .offset(y = (-20).dp)
                            .border(
                                width = 3.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFF9933), Color(0xFFFF5533))
                                ), RoundedCornerShape(100.dp)
                            )
                            .padding(3.dp)
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(null)
                                .build(),
                            contentDescription = "Profile Image",
                            placeholder = painterResource(R.drawable.farmer),
                            error = painterResource(R.drawable.farmer),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                LazyColumn {
                    items(listOfOptions) { item ->
                        SingleMenuItem(item.name, item.icon, onClick = item.onClick)
                    }
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    CustomButton(
                        modifier = Modifier.padding(top = 30.dp),
                        onClick = {},
                        text = "Logout", width = 1.0f
                    )
                }
            }
        }
    }
}

@Composable
fun SingleMenuItem(name: String, icon: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(25.dp)
            )
            Text(
                text = name,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        IconButton(onClick = {
            onClick()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}