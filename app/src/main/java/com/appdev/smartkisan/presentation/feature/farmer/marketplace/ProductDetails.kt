package com.appdev.smartkisan.presentation.feature.farmer.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appdev.smartkisan.R
import com.appdev.smartkisan.domain.model.Product
import com.appdev.smartkisan.presentation.Resuable.CustomSlider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetails(
    controller: NavHostController,
    product: com.appdev.smartkisan.domain.model.Product
) {
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
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
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
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    CustomSlider(
                        sliderList = product.imageUrls.toMutableList()
                    )
                }
                Text(
                    text = product.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 10.dp)
                )

                // Brand name
                if (product.brandName.isNotEmpty()) {
                    Text(
                        text = product.brandName,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.discountPrice > 0) {
                        Text(
                            text = "Rs. " + product.discountPrice.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSystemInDarkTheme()) Color(0xFF28B42F) else Color(
                                0xFF2E7D32
                            )
                        )
                        Text(
                            text = "Rs. " + product.price.toString(),
                            fontSize = 18.sp,
                            color = Color.Gray.copy(alpha = 0.8f),
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        Text(
                            text = "Rs. " + product.price.toString(),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSystemInDarkTheme()) Color(0xFF28B42F) else Color(
                                0xFF2E7D32
                            )
                        )
                    }
                }

                Text(
                    text = "Details",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 20.dp)
                )

                Divider(
                    color = Color.Gray.copy(alpha = 0.2f),
                    thickness = 2.dp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Basic details section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Quantity",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${product.weightOrVolume}/${product.unit ?: "Kg"}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                Divider(
                    color = Color.Gray.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Category-specific attributes
                when (product.category) {
                    "Seeds" -> {
                        // Show Seeds specific attributes
                        product.germinationRate?.let {
                            CategoryAttribute(
                                label = "Germination Rate",
                                value = "${it}%"
                            )
                        }

                        product.plantingSeason?.let { seasons ->
                            if (seasons.isNotEmpty()) {
                                CategoryAttribute(
                                    label = "Planting Season",
                                    value = seasons.joinToString(", ")
                                )
                            }
                        }

                        product.daysToHarvest?.let {
                            CategoryAttribute(
                                label = "Days to Harvest",
                                value = "$it days"
                            )
                        }
                    }
                    "Fertilizers" -> {
                        // Show Fertilizer specific attributes
                        product.applicationMethod?.let {
                            CategoryAttribute(
                                label = "Application Method",
                                value = it
                            )
                        }
                    }
                    "Medicine" -> {
                        // Show Medicine specific attributes
                        product.targetPestsOrDiseases?.let { targets ->
                            if (targets.isNotEmpty()) {
                                CategoryAttribute(
                                    label = "Target Pests/Diseases",
                                    value = targets.joinToString(", ")
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Description",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 20.dp)
                )

                Divider(
                    color = Color.Gray.copy(alpha = 0.2f),
                    thickness = 2.dp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = product.description,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryAttribute(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
    Divider(
        color = Color.Gray.copy(alpha = 0.2f),
        thickness = 1.dp,
        modifier = Modifier.padding(top = 4.dp)
    )
}

