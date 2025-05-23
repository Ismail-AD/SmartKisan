package com.appdev.smartkisan.presentation.feature.Seller.productmanagement

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.R
import com.appdev.smartkisan.Utils.Functions
import com.appdev.smartkisan.presentation.feature.Seller.store.StoreViewModel
import com.appdev.smartkisan.presentation.Resuable.CustomButton
import com.appdev.smartkisan.presentation.Resuable.CustomLoader
import com.appdev.smartkisan.presentation.theme.myGreen


@Composable
fun AddProductRoot(
    navHostController: NavHostController,
    storeViewModel: StoreViewModel
) {

    AddProductScreen(storeViewModel.productState, onAction = { action ->
        when (action) {
            is ProductActions.GoBack -> {
                navHostController.navigateUp()
            }

            else -> storeViewModel.onAction(action)
        }
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(productState: ProductState, onAction: (ProductActions) -> Unit) {
    val context = LocalContext.current
    var showToastState by remember { mutableStateOf(Pair(false, "")) }
    LaunchedEffect(productState.errorMessage) {
        productState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            onAction(ProductActions.ClearValidationError)
        }
    }
    LaunchedEffect(key1 = productState.uploaded) {
        if (productState.uploaded) {
            onAction(ProductActions.GoBack)
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Add Product",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onBackground
            )
        }, navigationIcon = {
            IconButton(onClick = {
                onAction.invoke(ProductActions.GoBack)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(23.dp)
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (productState.isLoading) {
                CustomLoader()
            }

            Column(
                modifier = Modifier
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(end = 15.dp, start = 15.dp, top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                TitledOutlinedTextField(
                    title = "Product Name",
                    value = productState.productName,
                    onValueChange = {
                        onAction.invoke(
                            ProductActions.ProductNameUpdated(
                                productName = it
                            )
                        )
                    },
                    placeholder = "Enter the name of product", singleLine = true
                )
                TitledOutlinedTextField(
                    title = "Brand Name",
                    value = productState.brandName,
                    onValueChange = {
                        onAction.invoke(
                            ProductActions.BrandUpdated(brand = it)
                        )
                    },
                    placeholder = "Enter the brand name of product", singleLine = true
                )

                TitledOutlinedTextField(
                    title = "Product Price",
                    value = productState.price,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            onAction.invoke(
                                ProductActions.PriceUpdated(
                                    price = it
                                )
                            )
                        }
                    },
                    placeholder = "Enter your product price",
                    singleLine = true, isNumber = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TitledOutlinedTextField(
                        title = "Quantity",
                        value = productState.quantity,
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                onAction.invoke(
                                    ProductActions.QuantityUpdated(
                                        quantity = it
                                    )
                                )
                            }
                        },
                        placeholder = "Enter quantity",
                        singleLine = true, isNumber = true
                    )
                    DropdownMenu(
                        title = "Product Type",
                        options = productState.categories,
                        selectedOption = productState.selectedCategory,
                        onOptionSelected = {
                            onAction.invoke(
                                ProductActions.CategoryUpdated(
                                    category = it
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                when (productState.selectedCategory) {
                    "Seeds" -> {
                        // Add planting season dropdown for Seeds
                        DropdownMenu(
                            title = "Planting Season",
                            options = productState.plantingSeasons,
                            selectedOption = productState.selectedPlantingSeason,
                            onOptionSelected = {
                                onAction.invoke(ProductActions.PlantingSeasonUpdated(season = it))
                            }
                        )
                    }

                    "Fertilizers" -> {
                        // Add application method dropdown for Fertilizers
                        DropdownMenu(
                            title = "Application Method",
                            options = productState.applicationMethods,
                            selectedOption = productState.selectedApplicationMethod,
                            onOptionSelected = {
                                onAction.invoke(ProductActions.ApplicationMethodUpdated(method = it))
                            }
                        )
                    }

                    "Medicine" -> {
                        Text(
                            text = "Diseases Treated",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 10.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        // Disease fields
                        productState.diseases.forEachIndexed { index, disease ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = disease,
                                    onValueChange = { newValue ->
                                        onAction.invoke(
                                            ProductActions.UpdateDisease(
                                                index,
                                                newValue
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Enter disease name") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.HealthAndSafety,
                                            contentDescription = "Disease",
                                            tint = myGreen
                                        )
                                    },
                                    colors = TextFieldDefaults.colors(
                                        unfocusedIndicatorColor = Color.LightGray,
                                        focusedIndicatorColor = myGreen,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        cursorColor = myGreen,
                                    ),
                                    singleLine = true
                                )

                                if (productState.diseases.size > 1) {
                                    IconButton(
                                        onClick = {
                                            onAction.invoke(ProductActions.RemoveDisease(index))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove Disease",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }

                        // Add new disease button
                        if (productState.diseases.size < 5) { // Limit to 5 diseases max
                            TextButton(
                                onClick = {
                                    if (productState.diseases.any { it.isBlank() }) {
                                        showToastState =
                                            Pair(true, "Fill the empty disease field first!")
                                    } else {
                                        onAction.invoke(ProductActions.AddDisease())
                                    }
                                },
                                modifier = Modifier.align(Alignment.End),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Disease",
                                    tint = myGreen
                                )
                                Text(
                                    text = "Add Another Disease",
                                    color = myGreen
                                )
                            }
                        }
                    }
                }


                TitledOutlinedTextField(
                    title = "Product Description",
                    value = productState.description,
                    onValueChange = {
                        onAction.invoke(
                            ProductActions.DescriptionUpdated(
                                description = it
                            )
                        )
                    },
                    placeholder = "Describe your product here",
                    height = 150
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TitledOutlinedTextField(
                        title = "Weight or Volume",
                        value = productState.weight,
                        onValueChange = { input ->
                            onAction.invoke(ProductActions.WeightUpdated(input.filter { it.isDigit() }))
                        },
                        placeholder = "e.g., 250",
                        modifier = Modifier.weight(1f), singleLine = true, isNumber = true
                    )

                    DropdownMenu(
                        title = "Measurement Type",
                        options = productState.measurements,
                        selectedOption = productState.measurement,
                        onOptionSelected = {
                            onAction.invoke(
                                ProductActions.MeasurementUpdated(
                                    measurement = it
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Product Images",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    productState.imageUris.forEachIndexed { index, uri ->
                        ImageSelector(
                            uri = uri,
                            index = index,
                            onImageSelect = { selectedUri, idx ->
                                onAction(ProductActions.SelectedImageUri(selectedUri, idx))
                            },
                            onImageRemove = { idx ->
                                // Add action to handle image removal
                                onAction(ProductActions.RemoveImage(idx))
                            }
                        )
                    }

                    if (productState.imageUris.size < 5) {
                        ImageSelector(
                            uri = null,
                            index = productState.imageUris.size,
                            onImageSelect = { selectedUri, idx ->
                                onAction(ProductActions.SelectedImageUri(selectedUri, idx))
                            }
                        )
                    }
                }
                CustomButton(
                    onClick = {
                        Log.d(
                            "CHJZAX", "Images changed : ${
                                Functions.haveImagesChanged(
                                    productState.imageUris,
                                    productState.initialUris
                                )
                            }"
                        )
                        if (Functions.haveImagesChanged(
                                productState.imageUris,
                                productState.initialUris
                            )
                        ) {
                            val imageBytesList: List<ByteArray?> =
                                productState.imageUris.mapNotNull { uri ->
                                    try {
                                        context.contentResolver.openInputStream(uri)?.use {
                                            it.readBytes()
                                        }
                                    } catch (e: Exception) {
                                        showToastState = Pair(true, "Failed to process image!")
                                        null
                                    }
                                }
                            if (productState.pid != null) {
                                onAction.invoke(
                                    ProductActions.UpdateTheProduct(
                                        listOfImageByteArrays = imageBytesList,
                                        productState.imageURLS
                                    )
                                )
                            } else {
                                onAction.invoke(ProductActions.AddToStore(listOfImageByteArrays = imageBytesList))
                            }
                        } else {
                            onAction.invoke(
                                ProductActions.UpdateTheProduct(
                                    null,
                                    productState.imageURLS
                                )
                            )
                        }
                    },
                    text = if (productState.pid != null) "Update Product" else "Save Product",
                    width = 1f,
                    modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                )
            }
            if (showToastState.first) {
                Toast.makeText(context, showToastState.second, Toast.LENGTH_SHORT).show()
                showToastState = Pair(false, "")
            }
        }
    }
}


@Composable
fun ImageSelector(
    uri: Uri?,
    index: Int,
    onImageSelect: (Uri, Int) -> Unit,
    onImageRemove: (Int) -> Unit = {} // New parameter for image removal
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri: Uri? ->
        if (selectedUri != null) {
            onImageSelect(selectedUri, index)
        }
    }

    Box(modifier = Modifier.size(110.dp)) {
        Card(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxSize(),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(uri)
                        .build(),
                    contentDescription = "Product Image",
                    placeholder = painterResource(R.drawable.placeimage),
                    error = painterResource(R.drawable.placeimage),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (uri != null) 0.dp else 35.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Only show remove button if there is an image
        if (uri != null) {
            if (uri != null) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .align(Alignment.TopEnd)
                        .clickable { onImageRemove(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Image",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun TitledOutlinedTextField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    isNumber: Boolean = false,
    height: Int = 56
) {

    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(height.dp),
            singleLine = singleLine,
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.LightGray,
                focusedIndicatorColor = myGreen,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                cursorColor = myGreen,
            ),
            placeholder = { Text(placeholder, color = Color.Gray) },
            keyboardOptions = if (isNumber) KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number) else KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Unspecified
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .padding(top = 10.dp)
                .border(
                    1.dp,
                    Color.LightGray,
                    RoundedCornerShape(5.dp)
                )
        ) {
            TextField(
                value = selectedOption,
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
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(5.dp),
                containerColor = MaterialTheme.colorScheme.background,
                border = BorderStroke(
                    1.dp, if (isSystemInDarkTheme()) Color(0xFF114646) else Color(
                        0xFFE4E7EE
                    )
                )
            ) {
                options.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        onClick = {
                            onOptionSelected(item)
                            expanded = false
                        },
                        modifier = Modifier.background(
                            if (selectedOption == item) if (isSystemInDarkTheme()) Color(
                                0xFF114646
                            ) else Color(
                                0xFFE4E7EE
                            ) else Color.Transparent
                        )
                    )
                }
            }
        }
    }
}
