package com.appdev.smartkisan.ui.MainAppScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appdev.smartkisan.R
import com.appdev.smartkisan.data.DiseaseResult
import com.appdev.smartkisan.data.Product
import com.appdev.smartkisan.ui.OtherComponents.SingleCrop
import com.appdev.smartkisan.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisResult(
    controller: NavHostController, diseaseResult: DiseaseResult = DiseaseResult(
        diseaseName = "Powdery Mildew",
        reasons = listOf(
            "High humidity levels and poor air circulation",
            "Overcrowded planting",
            "Susceptible plant variety",
            "Lack of sunlight"
        ), confirmation = listOf(
            "White, powdery spots on the leaves, stems, and flowers",
            "Leaves become distorted or turn yellow",
            "Stunted plant growth",
            "Fungal spores visible on affected areas"
        )
    )
) {
    val context = LocalContext.current
    val productList = listOf(
        Product(
            id = 1L,
            creatorId = "user123",
            name = "Herbal Medicine for Plants",
            price = 400.0,
            discountPrice = 350.0,
            imageUrls = listOf("https://example.com/seeds.jpg"),
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
            description = "Organic booster improves plant immunity and soil health...",
            quantity = 5L,
            weightOrVolume = 15.0f,
            updateTime = "2025-02-23T12:30:00Z",
            unit = "ml"
        )
    )

    val itemsRows by remember {
        mutableIntStateOf((productList.size + 2 - 1) / 2)
    }
    val heightToSet by remember {
        mutableIntStateOf(itemsRows * 250)
    }

    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            Card(onClick = {
                controller.navigateUp()
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
        })
    }) { paddings ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddings)
        ) {
            item {
                // Disease Image
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.placeholderdisease),
                        contentDescription = "Disease Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }

            item {
                // Disease Information
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "The plant shows signs of",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = diseaseResult.diseaseName,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            item {
                // Confirmation Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Confirm the Diagnosis",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 2.dp,
                        color = Color.Gray.copy(alpha = 0.2f)
                    )
                    diseaseResult.confirmation.forEach { item ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(
                                text = "•",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                color = Color(0xffA4A4A4),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            item {
                // Reasons Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Reasons behind the problem",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Divider(
                        color = Color.Gray.copy(alpha = 0.2f),
                        thickness = 2.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    diseaseResult.reasons.forEach { item ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(
                                text = "•",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                color = Color(0xffA4A4A4),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            item {
                // Products Section Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Suggested Products",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 2.dp,
                        color = Color.Gray.copy(alpha = 0.2f)
                    )
                }
            }

            item {
                // Products Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .height(heightToSet.dp)
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(productList) { product ->
                        SingleCrop(product,context) {
                            controller.navigate(Routes.ProductDetailScreen.route)
                        }
                    }
                }
            }
        }
    }
}