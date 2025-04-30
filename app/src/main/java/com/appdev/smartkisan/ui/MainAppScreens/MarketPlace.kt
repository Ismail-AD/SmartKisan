package com.appdev.smartkisan.ui.MainAppScreens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.appdev.smartkisan.Actions.MarketplaceActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.MarketplaceUiState
import com.appdev.smartkisan.ViewModel.MarketplaceViewModel
import com.appdev.smartkisan.data.Category
import com.appdev.smartkisan.data.Product
import com.appdev.smartkisan.ui.OtherComponents.CustomLoader
import com.appdev.smartkisan.ui.OtherComponents.SingleCrop
import com.appdev.smartkisan.ui.navigation.Routes
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Composable
fun MarketPlaceRoot(
    controller: NavHostController,
    marketplaceViewModel: MarketplaceViewModel = hiltViewModel()
) {
    val marketplaceState by marketplaceViewModel.marketplaceUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        marketplaceViewModel.onMarketplaceAction(MarketplaceActions.LoadProductsForUser)
    }

    MarketPlaceScreen(
        uiState = marketplaceState,
        onMarketplaceAction = { action ->
            when (action) {
                is MarketplaceActions.NavigateToProductDetail -> {
                    val productJson = Uri.encode(Json.encodeToString(action.product))
                    controller.navigate(Routes.ProductDetailScreen.route + "/$productJson")
                }

                else -> marketplaceViewModel.onMarketplaceAction(action)
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPlaceScreen(
    uiState: MarketplaceUiState,
    onMarketplaceAction: (MarketplaceActions) -> Unit
) {
    val context = LocalContext.current
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
                Text(text = "Market place", fontSize = 19.sp, fontWeight = FontWeight.Bold)
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
                LazyRow {
                    itemsIndexed(uiState.categories) { index, category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category.name,
                            onClick = {
                                onMarketplaceAction(MarketplaceActions.SelectCategory(category = category.name))
                            },
                            label = {
                                Text(
                                    text = category.name,
                                    modifier = Modifier.padding(
                                        end = 5.dp,
                                        top = 12.dp,
                                        bottom = 12.dp
                                    ),
                                    color = when (uiState.selectedCategory) {
                                        category.name -> Color.White
                                        else -> MaterialTheme.colorScheme.onBackground
                                    }
                                )
                            },
                            leadingIcon = {
                                Row {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Image(
                                        painter = painterResource(id = category.image),
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .padding(end = 10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.LightGray.copy(alpha = 0.4f),
                                selectedContainerColor = Color(0xff238b45),
                            ),
                            border = null
                        )
                    }
                }

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CustomLoader()
                        }
                    }

                    uiState.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(uiState.error)
                            onMarketplaceAction.invoke(MarketplaceActions.ClearValidationError)
                        }
                    }

                    uiState.filteredProducts.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No products found!",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.padding(top = 15.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.filteredProducts) { product ->
                                SingleCrop(product, context = context) {
                                    onMarketplaceAction(
                                        MarketplaceActions.NavigateToProductDetail(
                                            product = product
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}