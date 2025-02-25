package com.appdev.smartkisan.ui.SellerAppScreens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.R
import com.appdev.smartkisan.data.Product
import com.appdev.smartkisan.data.statusCard
import com.appdev.smartkisan.ui.OtherComponents.SingleCrop
import com.appdev.smartkisan.ui.navigation.Routes
import com.appdev.smartkisan.ui.theme.SmartKisanTheme
import com.appdev.smartkisan.ui.theme.myGreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerHomeScreen(controller: NavHostController) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val listOfOptions by remember {
        mutableStateOf(
            listOf(
                statusCard(name = "Products", counter = 10L),
                statusCard(name = "Avg. Rating", counter = 5L),
                statusCard(name = "Total Product Views", counter = 15L),
                statusCard(name = "Products", counter = 10L),
                statusCard(name = "Avg. Rating", counter = 5L),
                statusCard(name = "Total Product Views", counter = 15L)
            )
        )
    }

    val productList = listOf(
        Product(
            id = 1L,
            creatorId = "user123",
            name = "Herbal Medicine for Plants",
            price = 400.0,
            discountPrice = 350.0,
            imageUrls = listOf("https://example.com/seeds.jpg"),
            ratings = 4.7f,
            reviewsCount = 85L,
            description = "This herbal plant medicine is specially formulated to enhance growth and protect your plants from common diseases...",
            quantity = 10L,
            weightOrVolume = 10.0f,
            updateTime = "2025-02-23T12:00:00Z",
            unit = "g"
        ),
        Product(
            id = 2L,
            creatorId = "user124",
            name = "Organic Plant Booster",
            price = 500.0,
            discountPrice = 450.0,
            imageUrls = listOf("https://example.com/booster.jpg"),
            ratings = 4.5f,
            reviewsCount = 120L,
            description = "Organic booster improves plant immunity and soil health...",
            quantity = 5L,
            weightOrVolume = 15.0f,
            updateTime = "2025-02-23T12:30:00Z",
            unit = "ml"
        )
    )
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Dashboard",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        })
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
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Welcome Jamil",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                Color.LightGray, RoundedCornerShape(100.dp)
                            )
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedImageUri)
                                .build(),
                            contentDescription = "Profile Image",
                            placeholder = painterResource(R.drawable.farmer),
                            error = painterResource(R.drawable.farmer),
                            modifier = Modifier
                                .size(35.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOfOptions.forEachIndexed { index, statusCard ->
                        StatusCard(index, statusCard)
                    }
                }
                Card(
                    onClick = {
                        controller.navigate(Routes.StoreManagementScreen.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp, bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) Color(
                            0xFF114646
                        ) else myGreen
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .padding(start = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Store Management",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Update inventory, prices & product details",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f), lineHeight = 21.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    Color.White, RoundedCornerShape(50.dp)
                                )
                                .padding(15.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.shops),
                                modifier = Modifier
                                    .size(50.dp), contentDescription = "",
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Text(
                    text = "Popular Products",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground, modifier = Modifier
                        .padding(top = 10.dp)
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    thickness = 2.dp,
                    color = Color.Gray.copy(alpha = 0.2f)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productList) { item ->
                        SingleCrop(item, Modifier.width(180.dp)) { }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusCard(
    index: Int,
    statusCard: statusCard
) {

    val iconColor = getDynamicColor(index)
    val cardBackgroundColor = iconColor.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.2f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp, end = 13.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            iconColor, RoundedCornerShape(100.dp)
                        )
                        .clip(CircleShape)
                        .padding(5.dp)
                ) {
                    Image(
                        painter = painterResource(id = getIconResource(statusCard.name)),
                        contentDescription = statusCard.name,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = statusCard.name,
                    fontSize = 17.sp, color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = statusCard.counter.toString(),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 4.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun getDynamicColor(index: Int): Color {
    return if (isSystemInDarkTheme()) {
        randomDynamicNightColor(index)
    } else {
        randomColorForPattern(index)
    }
}

fun randomColorForPattern(index: Int): Color {
    return when (index % 3) {
        0 -> Color(
            android.graphics.Color.argb(
                255,
                (180..220).random(),
                (140..180).random(),
                (50..90).random()
            )
        ) // Dark yellowish
        1 -> Color(
            android.graphics.Color.argb(
                255,
                (60..100).random(),
                (90..140).random(),
                (150..200).random()
            )
        ) // Dark bluish
        else -> Color(
            android.graphics.Color.argb(
                255,
                (150..200).random(),
                (50..90).random(),
                (50..90).random()
            )
        ) // Dark reddish
    }
}

fun randomDynamicNightColor(index: Int): Color {
    return when (index % 3) {
        0 -> Color(
            android.graphics.Color.argb(
                255,
                (90..130).random(), // Low brightness for red
                (70..110).random(), // Low brightness for green
                (40..80).random()  // Low brightness for blue
            )
        )

        1 -> Color(
            android.graphics.Color.argb(
                255,
                (40..90).random(),
                (60..120).random(),
                (100..160).random() // Cooler blue tones
            )
        )

        else -> Color(
            android.graphics.Color.argb(
                255,
                (100..150).random(), // Soft reds
                (50..90).random(),
                (50..90).random()
            )
        )
    }
}


private fun getIconResource(iconName: String): Int {

    return when (iconName) {
        "Products" -> R.drawable.box
        "Avg. Rating" -> R.drawable.star
        "Total Product Views" -> R.drawable.visibility
        else -> R.drawable.box
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreviewy() {
    SmartKisanTheme {
        SellerHomeScreen(controller = rememberNavController())
    }
}
