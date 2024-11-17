package com.appdev.smartkisan.ui.MainAppScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appdev.smartkisan.R
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.appdev.smartkisan.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDisease(controller: NavHostController) {

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
                Text(text = "Disease Detection", fontSize = 19.sp, fontWeight = FontWeight.Bold)
            }
        }, modifier = Modifier.shadow(2.dp))
    }) { paddings ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddings),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF66BB6A))) {
                    Text(
                        text = "Instructions: Make sure the whole leaf is clear and looks like the sample shown and put the leaf on a plain background for better results.",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    border = BorderStroke(
                        width = 4.dp,
                        color = Color(0xFF2E7D32)
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.placeholderdisease),
                        contentDescription = "", modifier = Modifier.size(200.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                imageGet(
                    text = "Take picture",
                    subtitle = "of your plant",
                    icon = R.drawable.camshot
                ) {}
                imageGet(
                    text = "Import picture",
                    subtitle = "from your gallery",
                    icon = R.drawable.importgallery
                ) {}
                Spacer(modifier = Modifier.height(20.dp))
                CustomButton(onClick = {
                    controller.navigate(Routes.DiagnosisResult.route)
                }, text = "Diagnose", width = 1f)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun imageGet(text: String, subtitle: String, icon: Int, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xff8ad167)),
        modifier = Modifier.padding(top = 10.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = text,
                    fontSize = 19.sp, color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp, color = Color(0xFF2E7D32)
                )
            }
            Image(
                painter = painterResource(id = icon),
                contentDescription = "",
                modifier = Modifier.size(40.dp) // Size of the actual image
            )
        }
    }
}