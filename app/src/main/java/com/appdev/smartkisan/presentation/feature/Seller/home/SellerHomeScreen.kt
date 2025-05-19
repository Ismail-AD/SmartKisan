package com.appdev.smartkisan.presentation.feature.Seller.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.appdev.smartkisan.R
import com.appdev.smartkisan.domain.model.statusCard
import com.appdev.smartkisan.presentation.Resuable.NoDialogLoader
import com.appdev.smartkisan.presentation.Resuable.SingleCrop
import com.appdev.smartkisan.presentation.navigation.Routes
import com.appdev.smartkisan.presentation.theme.myGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerHomeScreen(
    controller: NavHostController,
    viewModel: SellerDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
        viewModel.setUserData()
    }


    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Hello ${uiState.userName} \uD83D\uDC4B",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }, actions = {
                Box(modifier = Modifier.padding(end = 12.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(uiState.userImage)
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
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
        }
    ) { innerPadding ->
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
                    .verticalScroll(rememberScrollState())
            ) {
                // Status cards in horizontal row
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    uiState.statusCards.forEachIndexed { index, statusCard ->
                        StatusCard(index, statusCard)
                    }
                }

                // Store Management card
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
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 21.sp
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
                                    .size(50.dp),
                                contentDescription = "Store Icon",
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Popular Products section
                Text(
                    text = "Recent Products",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 10.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(top = 4.dp),
                    thickness = 2.dp,
                    color = Color.Gray.copy(alpha = 0.2f)
                )

                // Loading state
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        NoDialogLoader("Loading Recent Products...")
                    }
                }
                // Error state
                else if (uiState.errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                // Empty state
                else if (uiState.products.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.box),
                                contentDescription = "No Products",
                                modifier = Modifier.size(48.dp),
                                alpha = 0.5f
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No products available",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap 'Store Management' to add products",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
                // Products list
                else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.products) { product ->
                            SingleCrop(
                                product = product,
                                context = context,
                                modifier = Modifier.width(180.dp)
                            ) { }
                        }
                    }
                }

                // Add some bottom padding
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StatusCard(
    index: Int,
    statusCard: com.appdev.smartkisan.domain.model.statusCard
) {
    val iconColor = getDynamicColor(index)
    val cardBackgroundColor = iconColor.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.2f)

    Card(
        modifier = Modifier
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
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = if (statusCard.name == "Revenue") "₹${statusCard.counter}" else statusCard.counter.toString(),
                style = TextStyle(
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
        else -> R.drawable.box
    }
}