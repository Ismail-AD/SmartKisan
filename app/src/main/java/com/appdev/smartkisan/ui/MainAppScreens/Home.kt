package com.appdev.smartkisan.ui.MainAppScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appdev.smartkisan.R
import com.appdev.smartkisan.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(controller: NavHostController) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tempicon),
                    contentDescription = "",
                    modifier = Modifier.size(40.dp)
                )
                Text(text = "Smart Kisan", fontSize = 19.sp, fontWeight = FontWeight.Bold)
            }
        }, actions = {
            IconButton(onClick = {
                controller.navigate(Routes.ChatBotScreen.route)
            }) {
                Image(
                    painter = painterResource(id = R.drawable.chatbot),
                    contentDescription = "",
                    modifier = Modifier.size(31.dp)
                )
            }
        }, modifier = Modifier.shadow(2.dp))
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
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(start = 3.dp)
                ) {
                    Text(
                        text = "Hello Jamil,",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Image(
                        painter = painterResource(id = R.drawable.farmer),
                        contentDescription = "",
                        modifier = Modifier.size(28.dp)
                    )
                }
                WeatherCard()
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