package com.appdev.smartkisan.presentation.feature.farmer.diseasedetection.DiagnosisResult

import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.appdev.smartkisan.presentation.feature.farmer.diseasedetection.DiseaseDetectActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.presentation.feature.farmer.diseasedetection.DiseaseDetectState
import com.appdev.smartkisan.presentation.feature.farmer.diseasedetection.DiseaseDetectViewModel
import com.appdev.smartkisan.domain.model.Product
import com.appdev.smartkisan.presentation.Resuable.DotsLoading
import com.appdev.smartkisan.presentation.Resuable.SingleCrop
import com.appdev.smartkisan.presentation.feature.farmer.marketplace.MarketplaceActions
import com.appdev.smartkisan.presentation.navigation.Routes
import kotlinx.serialization.json.Json

@Composable
fun DiagnosisResultRoot(
    controller: NavHostController,
    diseaseDetectViewModel: DiseaseDetectViewModel = hiltViewModel()
) {
    val detectUiState by diseaseDetectViewModel.detectUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        detectUiState.diseaseDetails?.let { diseaseResult ->
            if (detectUiState.suggestedProducts.isEmpty() && detectUiState.productsError == null) {
                diseaseDetectViewModel.onAction(
                    DiseaseDetectActions.FetchProductsForDisease(diseaseResult.diseaseName)
                )
            }
        }
    }

    DiagnosisResult(detectUiState) { action ->
        when (action) {
            is DiseaseDetectActions.GoBack -> {
                diseaseDetectViewModel.onAction(DiseaseDetectActions.ClearData)
                controller.navigateUp()
            }

            is DiseaseDetectActions.NavigateToProductDetail -> {
                val productJson = Uri.encode(Json.encodeToString(action.product))
                controller.navigate(Routes.ProductDetailScreen.route + "/$productJson")
            }


            else -> diseaseDetectViewModel.onAction(action)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisResult(
    detectUiState: DiseaseDetectState, onAction: (DiseaseDetectActions) -> Unit
) {
    val context = LocalContext.current

    val itemsRows by remember {
        mutableIntStateOf((detectUiState.suggestedProducts.size + 2 - 1) / 2)
    }
    val heightToSet by remember {
        mutableIntStateOf(itemsRows * 250)
    }


    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Diagnosis Results",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }, navigationIcon = {
            IconButton(onClick = { onAction(DiseaseDetectActions.GoBack) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
    }) { paddings ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddings)
        ) {
            item {
                // Header with image and disease name
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        // Image without awkward border
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(detectUiState.selectedImageBitmap)
                                .build(),
                            placeholder = painterResource(id = R.drawable.placeholderdisease),
                            error = painterResource(id = R.drawable.placeholderdisease),
                            contentDescription = "Plant Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(270.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.FillBounds
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Disease Name + Status
                        detectUiState.diseaseDetails?.let { disease ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = disease.diseaseName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Confidence below disease name
                            if (disease.confidence > 0f) {
                                Text(
                                    text = "Confidence: ${disease.confidence}%",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (disease.diseaseName.contains(
                                        "Healthy",
                                        ignoreCase = true
                                    )
                                ) "No treatment needed" else "Treatment recommended",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Uncommented and improved causes section
            item {
                detectUiState.diseaseDetails?.let { disease ->
                    if (disease.causedBy.isNotEmpty() && (!disease.diseaseName.contains(
                            "Healthy",
                            ignoreCase = true
                        )
                                )
                    ) {
                        DiagnosisSection(
                            title = "Possible Causes",
                            items = disease.causedBy,
                            iconTint = Color(0xFF2196F3),
                        )
                    }
                }
            }

            // Uncommented and improved treatments section
            item {
                detectUiState.diseaseDetails?.let { disease ->
                    if (disease.treatments.isNotEmpty() && (!disease.diseaseName.contains(
                            "Healthy",
                            ignoreCase = true
                        )
                                )
                    ) {
                        DiagnosisSection(
                            title = "Recommended Treatments",
                            items = disease.treatments,
                            iconTint = Color(0xFFFF9800),
                        )
                    }
                }
            }

            item {
                // Products Section Header
                if (detectUiState.diseaseDetails != null && !detectUiState.diseaseDetails.diseaseName.contains(
                        "Healthy",
                        ignoreCase = true
                    )
                ) {
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
            }

            item {
                if (detectUiState.isLoadingProducts) {
                    DotsLoading(
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                    )
                }
            }

            // Product error state
            item {
                if(detectUiState.diseaseDetails != null && !detectUiState.diseaseDetails.diseaseName.contains(
                        "Healthy",
                        ignoreCase = true
                    )){
                    detectUiState.productsError?.let { error ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = error,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Products Grid - Only show if we have products
            item {
                if (detectUiState.suggestedProducts.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .height(heightToSet.dp)
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(detectUiState.suggestedProducts) { product ->
                            SingleCrop(product, context) {
                                onAction(
                                    DiseaseDetectActions.NavigateToProductDetail(
                                        product = product
                                    )
                                )
                            }
                        }
                    }
                } else if (!detectUiState.isLoadingProducts && detectUiState.productsError == null && detectUiState.diseaseDetails != null && !detectUiState.diseaseDetails.diseaseName.contains(
                        "Healthy",
                        ignoreCase = true
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No specific products found for this disease",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosisSection(
    title: String,
    items: List<String>,
    iconTint: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Enhanced bullet point items
            items.forEach { item ->
                BulletPointItem(item, iconTint)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BulletPointItem(text: String, bulletColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        // Improved bullet point
        Box(
            modifier = Modifier
                .padding(top = 6.dp, end = 8.dp)
                .size(8.dp)
                .background(bulletColor, CircleShape)
        )

        // Text with proper wrapping
        Text(
            text = text,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            modifier = Modifier.weight(1f)
        )
    }
}