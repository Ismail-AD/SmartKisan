package com.appdev.smartkisan.ui.SignUpProcess

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdev.smartkisan.R
import com.appdev.smartkisan.ui.OtherComponents.CustomButton

@Composable
fun UserSelection(buttonClick:()->Unit) {
    var selectedRole by remember {
        mutableIntStateOf(0)
    }
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(13.dp), modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Text(text = "Who Will You Be ?",fontWeight = FontWeight.Bold, fontSize = 23.sp)
            Spacer(modifier = Modifier.height(8.dp))
            selectionCard(
                title = "Farmer",
                features = "Disease detection, weather updates, AI chatbot, daily crop suggestions, and more",
                image = R.drawable.farmerrole,
                isSelected = selectedRole == 0
            ) {
                selectedRole = 0
            }
            selectionCard(
                title = "Seller",
                features = "Manage store, sell seeds and fertilizers, upload products, connect with farmers, and more",
                image = R.drawable.sellerroleplay,
                isSelected = selectedRole == 1
            ) {
                selectedRole = 1
            }
            Spacer(modifier = Modifier.height(13.dp))
            CustomButton(
                onClick = { buttonClick() },
                text = "Next", width = 1f
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun selectionCard(
    title: String,
    features: String,
    image: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .height(160.dp),
        shape = RoundedCornerShape(8.dp), // Adjust the corner radius as needed
        border = BorderStroke(
            width = if (isSelected) 2.dp else 0.dp,
            color = if (isSelected) Color(0xff0FAACB) else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "",
                modifier = Modifier.weight(1.7f), contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.weight(2f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                Text(text = features, lineHeight = 19.sp, fontSize = 15.sp)
            }
        }
    }
}