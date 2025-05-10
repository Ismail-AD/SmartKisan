package com.appdev.smartkisan.ui.SellerAppScreens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.appdev.smartkisan.Actions.SellerStoreActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.StoreUiState
import com.appdev.smartkisan.ViewModel.StoreViewModel
import com.appdev.smartkisan.ui.OtherComponents.ExpandedItem
import com.appdev.smartkisan.ui.OtherComponents.SearchField
import com.appdev.smartkisan.ui.OtherComponents.CustomLoader
import com.appdev.smartkisan.ui.OtherComponents.NoDialogLoader
import com.appdev.smartkisan.ui.navigation.Routes
import com.appdev.smartkisan.ui.theme.myGreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Composable
fun StoreManagementRoot(
    navHostController: NavHostController,
    storeViewModel: StoreViewModel = hiltViewModel()
) {
    val productListState by storeViewModel.storeUiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        storeViewModel.onStoreAction(SellerStoreActions.LoadProductsForAdmin)
    }
    StoreManagementScreen(productListState, onStoreAction = { action ->
        when (action) {
            is SellerStoreActions.NavigateToAddProduct -> {
                val route = if (action.product != null) {
                    val productJson = Uri.encode(Json.encodeToString(action.product))
                    Routes.AddProductScreen.route + "/$productJson"
                } else {
                    Routes.AddProductScreen.route+ "/{}"
                }
                navHostController.navigate(route)
            }

            is SellerStoreActions.NavigateToProductDetail -> {
                val productJson = Uri.encode(Json.encodeToString(action.product))
                navHostController.navigate(Routes.ProductDetailScreen.route + "/$productJson")
            }

            is SellerStoreActions.NavigateBack -> {
                navHostController.navigateUp()
            }

            else -> storeViewModel.onStoreAction(action)
        }
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreManagementScreen(
    uiState: StoreUiState,
    onStoreAction: (SellerStoreActions) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inventory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onStoreAction(SellerStoreActions.NavigateBack)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                },                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onStoreAction(SellerStoreActions.NavigateToAddProduct(null)) },
                containerColor = myGreen,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 15.dp)
            ) {
                // Product list
                when {
                    uiState.isLoading -> {
                        NoDialogLoader("Loading Products...")
                    }

                    uiState.error != null -> {
                        Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
                        onStoreAction.invoke(SellerStoreActions.ClearValidationError)

                    }

                    uiState.products.isEmpty() -> {
                        // Show empty state
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
                        val filteredProducts = uiState.filteredProducts.filter { product ->
                            val matchesCategory = uiState.selectedCategory == "All" ||
                                    product.category.contains(
                                        uiState.selectedCategory,
                                        ignoreCase = true
                                    )
                            val matchesQuery = uiState.searchQuery.isEmpty() ||
                                    product.name.contains(uiState.searchQuery, ignoreCase = true)

                            matchesCategory && matchesQuery
                        }
                        // Search field
                        SearchField(
                            query = uiState.searchQuery,
                            placeholder = "Search Products",
                            modifier = Modifier.fillMaxWidth(),
                            onFocusChange = { },
                            onTextChange = { text ->
                                onStoreAction(SellerStoreActions.SetSearchQuery(text))
                            },
                            onBackClick = {
                                focusManager.clearFocus()
                                onStoreAction(SellerStoreActions.ClearSearchQuery)
                            }
                        )

                        // Category dropdown
                        ExposedDropdownMenuBox(
                            expanded = uiState.isDropdownExpanded,
                            onExpandedChange = {
                                onStoreAction(SellerStoreActions.ToggleDropdown(it))
                            },
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .border(
                                    (1.5).dp,
                                    if (isSystemInDarkTheme()) Color(0xFF114646) else Color(0xFFE4E7EE),
                                    RoundedCornerShape(5.dp)
                                )
                        ) {
                            TextField(
                                value = uiState.selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(10.dp),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isDropdownExpanded)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = uiState.isDropdownExpanded,
                                onDismissRequest = {
                                    onStoreAction(SellerStoreActions.ToggleDropdown(false))
                                },
                                shape = RoundedCornerShape(5.dp),
                                containerColor = MaterialTheme.colorScheme.background,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSystemInDarkTheme()) Color(0xFF114646) else Color(0xFFE4E7EE)
                                )
                            ) {
                                uiState.categories.forEachIndexed { index, category ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = category,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        },
                                        onClick = {
                                            onStoreAction(SellerStoreActions.SelectCategory(category))
                                        },
                                        modifier = Modifier.background(
                                            if (uiState.selectedCategory == category)
                                                if (isSystemInDarkTheme()) Color(0xFF114646) else Color(
                                                    0xFFE4E7EE
                                                )
                                            else Color.Transparent
                                        )
                                    )
                                    if (index != uiState.categories.lastIndex && category != uiState.selectedCategory) {
                                        HorizontalDivider(
                                            thickness = 1.dp,
                                            color = Color.Gray.copy(alpha = if (isSystemInDarkTheme()) 0.6f else 0.2f)
                                        )
                                    }
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.padding(top = 15.dp),
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            itemsIndexed(filteredProducts) { index, product ->
                                ExpandedItem(context, product, onDelete = {
                                    onStoreAction.invoke(SellerStoreActions.DeleteProduct(it))
                                }, onUpdate = {
                                    onStoreAction.invoke(SellerStoreActions.NavigateToAddProduct(it))
                                }) {
                                    onStoreAction.invoke(
                                        SellerStoreActions.NavigateToProductDetail(
                                            product
                                        )
                                    )
                                }
                                if (index == filteredProducts.lastIndex) {
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}