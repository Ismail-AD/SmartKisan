package com.appdev.smartkisan.presentation.feature.Seller.store

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.presentation.feature.Seller.productmanagement.ProductActions
import com.appdev.smartkisan.data.repository.Repository
import com.appdev.smartkisan.presentation.feature.Seller.productmanagement.ProductState
import com.appdev.smartkisan.Utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    val repository: Repository,
) : ViewModel() {

    var productState by mutableStateOf(ProductState())
        private set

    private val _uiState = MutableStateFlow(StoreUiState(isLoading = true))
    val storeUiState = _uiState.asStateFlow()

    fun initializeWithProduct(product: com.appdev.smartkisan.domain.model.Product) {
        productState = ProductState(
            pid = product.id,
            productName = product.name,
            price = product.price.toString(),
            description = product.description,
            quantity = product.quantity.toString(),
            weight = product.weightOrVolume.toString(),
            measurement = product.unit ?: "",
            selectedCategory = product.category,
            // Initialize category-specific attributes
            selectedApplicationMethod = product.applicationMethod ?: "Spray",
            selectedPlantingSeason = product.plantingSeason?.firstOrNull() ?: "Spring",
            diseases = product.targetPestsOrDiseases ?: listOf(""),
            imageUris = product.imageUrls.map { Uri.parse(it) },
            initialUris = product.imageUrls.map { Uri.parse(it) },
            imageURLS = product.imageUrls
        )
    }


    private fun fetchProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getProducts().collect { result ->
                _uiState.value = when (result) {
                    is ResultState.Success -> {
                        _uiState.value.copy(
                            products = result.data,
                            filteredProducts = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is ResultState.Failure -> {
                        _uiState.value.copy(
                            isLoading = false,
                            error = result.msg.localizedMessage
                        )
                    }

                    ResultState.Loading -> {
                        _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }
    }

    fun updateProduct(
        product: com.appdev.smartkisan.domain.model.Product, imageByteArrays: List<ByteArray?>?,
        imageUris: List<Uri?>?
    ) {
        viewModelScope.launch {
            productState = productState.copy(isLoading = true)
            repository.updateProduct(product, imageByteArrays, imageUris).collect { result ->
                productState = when (result) {
                    is ResultState.Success -> {
                        productState.copy(
                            isLoading = false,
                            errorMessage = result.data,
                            uploaded = true
                        )
                    }

                    is ResultState.Failure -> {
                        productState.copy(
                            isLoading = false,
                            errorMessage = result.msg.localizedMessage
                        )
                    }

                    ResultState.Loading -> {
                        productState.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.deleteProduct(productId).collect { result ->
                _uiState.value = when (result) {
                    is ResultState.Success -> {
                        val updatedProductList =
                            _uiState.value.products.filter { it.id != productId }
                        _uiState.value.copy(
                            products = updatedProductList,
                            filteredProducts = updatedProductList,
                            isLoading = false,
                            error = result.data
                        )
                    }

                    is ResultState.Failure -> {
                        _uiState.value.copy(
                            isLoading = false,
                            error = result.msg.localizedMessage
                        )
                    }

                    ResultState.Loading -> {
                        _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }
    }

    fun onStoreAction(action: SellerStoreActions) {
        when (action) {

            is SellerStoreActions.LoadProductsForAdmin-> fetchProducts()
            is SellerStoreActions.SelectCategory -> updateCategory(action.category)
            is SellerStoreActions.SetSearchQuery -> updateSearchQuery(action.query)
            is SellerStoreActions.ToggleDropdown -> toggleDropdown(action.expanded)
            is SellerStoreActions.ClearSearchQuery -> clearSearchQuery()
            is SellerStoreActions.DeleteProduct -> deleteProduct(action.pid)
            is SellerStoreActions.ClearValidationError -> clearMessage()
            else -> {}
        }
    }

    private fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            error = null
        )
    }

    private fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            isDropdownExpanded = false
        )
    }

    private fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    private fun toggleDropdown(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(isDropdownExpanded = expanded)
    }

    private fun clearSearchQuery() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
    }

    fun onAction(action: ProductActions) {
        when (action) {
            is ProductActions.CategoryUpdated -> {
                // Reset category-specific fields when category changes
                productState = when (action.category) {
                    "Seeds" -> productState.copy(
                        selectedCategory = action.category,
                        selectedPlantingSeason = "Spring"
                    )
                    "Fertilizers" -> productState.copy(
                        selectedCategory = action.category,
                        selectedApplicationMethod = "Spray"
                    )
                    "Medicine" -> productState.copy(
                        selectedCategory = action.category,
                        diseases = listOf(""),
                    )
                    else -> productState.copy(selectedCategory = action.category)
                }
            }

            // New action handlers
            is ProductActions.ApplicationMethodUpdated -> {
                productState = productState.copy(selectedApplicationMethod = action.method)
            }

            is ProductActions.PlantingSeasonUpdated -> {
                productState = productState.copy(selectedPlantingSeason = action.season)
            }

            is ProductActions.AddDisease -> {
                val updatedDiseases = productState.diseases.toMutableList()
                updatedDiseases.add(action.disease)
                productState = productState.copy(diseases = updatedDiseases)
            }

            is ProductActions.UpdateDisease -> {
                val updatedDiseases = productState.diseases.toMutableList()
                if (action.index < updatedDiseases.size) {
                    updatedDiseases[action.index] = action.disease
                    productState = productState.copy(diseases = updatedDiseases)
                }
            }

            is ProductActions.RemoveDisease -> {
                if (productState.diseases.size > 1) { // Keep at least one disease field
                    val updatedDiseases = productState.diseases.toMutableList()
                    updatedDiseases.removeAt(action.index)
                    productState = productState.copy(diseases = updatedDiseases)
                }
            }

            ProductActions.GoBack -> {
            }

            is ProductActions.ProductNameUpdated -> {
                productState = productState.copy(productName = action.productName)
            }

            is ProductActions.PriceUpdated -> {
                productState = productState.copy(price = action.price)
            }
            is ProductActions.BrandUpdated-> {
                productState = productState.copy(brandName = action.brand)
            }


            is ProductActions.DescriptionUpdated -> {
                productState = productState.copy(description = action.description)
            }

            is ProductActions.WeightUpdated -> {
                productState = productState.copy(weight = action.weight)
            }

            is ProductActions.MeasurementUpdated -> {
                productState = productState.copy(measurement = action.measurement)
            }

            is ProductActions.SelectedImageUri -> {
                val currentUris = productState.imageUris.toMutableList()
                if (action.index < currentUris.size) {
                    currentUris[action.index] = action.uri
                } else {
                    currentUris.add(action.uri)
                }
                productState = productState.copy(imageUris = currentUris)
            }

            is ProductActions.QuantityUpdated -> {
                productState = productState.copy(quantity = action.quantity)

            }

            is ProductActions.AddToStore -> {
                val validationMessage = validateProductFields()
                if (validationMessage != null) {
                    productState = productState.copy(errorMessage = validationMessage)
                    return
                }
                val price = productState.price.toDoubleOrNull() ?: 0.0
                val quantity = productState.quantity.toLongOrNull() ?: 0L
                val weight = productState.weight.toFloatOrNull() ?: 0f

                // Create base product with common fields
                val product = com.appdev.smartkisan.domain.model.Product(
                    name = productState.productName,
                    price = price,
                    description = productState.description,
                    quantity = quantity,
                    weightOrVolume = weight,
                    unit = productState.measurement,
                    category = productState.selectedCategory,
                    brandName = productState.brandName
                )

                // Add category-specific attributes based on selectedCategory
                when (productState.selectedCategory) {
                    "Seeds" -> {
                        product.germinationRate = 0f  // Default value, replace if you have this in state
                        product.plantingSeason = listOf(productState.selectedPlantingSeason)
                        product.daysToHarvest = 0L    // Default value, replace if you have this in state
                    }
                    "Fertilizers" -> {
                        product.applicationMethod = productState.selectedApplicationMethod
                    }
                    "Medicine" -> {
                        // Filter out any empty disease entries
                        product.targetPestsOrDiseases = productState.diseases.filter { it.isNotBlank() }
                    }
                }


                viewModelScope.launch {
                    repository.addProduct(
                        product = product,
                        imageByteArrays = action.listOfImageByteArrays,
                        imageUris = productState.imageUris
                    ).collect { result ->
                        productState = when (result) {
                            is ResultState.Failure -> productState.copy(
                                errorMessage = result.msg.localizedMessage,
                                isLoading = false
                            )

                            ResultState.Loading -> productState.copy(isLoading = true)
                            is ResultState.Success -> {
                                productState.copy(
                                    errorMessage = result.data,
                                    uploaded = true,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            }

            is ProductActions.RemoveImage -> {
                // Get the current list of images
                val currentImages = productState.imageUris.toMutableList()

                // Remove the image at the specified index
                if (action.index < currentImages.size) {
                    currentImages.removeAt(action.index)
                    // Update the state with the new list
                    productState = productState.copy(imageUris = currentImages)
                }
            }

            ProductActions.ClearValidationError -> {
                productState = productState.copy(errorMessage = null)

            }


            is ProductActions.UpdateTheProduct -> {
                val validationMessage = validateProductFields()
                if (validationMessage != null) {
                    productState = productState.copy(errorMessage = validationMessage)
                    return
                }
                Log.d("CHJZAX", "Images Urls : ${action.imageUrls}")
                productState.pid?.let { productId ->
                    updateProduct(
                        com.appdev.smartkisan.domain.model.Product(
                            id = productId,
                            name = productState.productName,
                            price = productState.price.toDouble(),
                            description = productState.description,
                            quantity = productState.quantity.toLong(),
                            weightOrVolume = productState.weight.toFloat(),
                            unit = productState.measurement,
                            category = productState.selectedCategory,
                            imageUrls = action.imageUrls
                        ),
                        imageByteArrays = action.listOfImageByteArrays,
                        imageUris = productState.imageUris
                    )
                }
            }
        }
    }

    private fun validateProductFields(): String? {
        return when {
            productState.productName.isBlank() -> "Product name cannot be empty."
            productState.brandName.isBlank() -> "Brand name cannot be empty."
            productState.description.isBlank() -> "Product description cannot be empty."
            productState.price.isBlank() -> "Price cannot be empty."
            productState.price.toDoubleOrNull() == null -> "Price must be a valid number."
            productState.price.toDoubleOrNull()!! <= 0 -> "Price must be greater than zero."
            productState.quantity.isBlank() -> "Quantity cannot be empty."
            productState.quantity.toLongOrNull() == null -> "Quantity must be a valid number."
            productState.quantity.toLongOrNull()!! <= 0 -> "Quantity must be greater than zero."
            productState.weight.isBlank() -> "Weight cannot be empty."
            productState.weight.toFloatOrNull() == null -> "Weight must be a valid number."
            productState.weight.toFloatOrNull()!! <= 0 -> "Weight must be greater than zero."
            productState.measurement.isBlank() -> "Measurement unit cannot be empty."
            productState.imageUris.isEmpty() -> "At least one product image is required."
            productState.diseases.any { it.isBlank() } && productState.selectedCategory == "Medicine" ->
                "All disease fields must be filled for medicine products."
            else -> null
        }
    }
}