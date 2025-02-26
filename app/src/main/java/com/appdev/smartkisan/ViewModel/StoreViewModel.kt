package com.appdev.smartkisan.ViewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.ProductActions
import com.appdev.smartkisan.Actions.StoreActions
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.States.ProductState
import com.appdev.smartkisan.States.StoreUiState
import com.appdev.smartkisan.Utils.Functions
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.data.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun initializeWithProduct(product: Product) {
        productState = ProductState(
            pid = product.id,
            productName = product.name,
            price = product.price.toString(),
            description = product.description,
            quantity = product.quantity.toString(),
            weight = product.weightOrVolume.toString(),
            measurement = product.unit ?: "",
            selectedCategory = product.category,
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
        product: Product, imageByteArrays: List<ByteArray?>?,
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
                        val filteredList = _uiState.value.products.filter { it.id != productId }
                        _uiState.value.copy(
                            products = filteredList,
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

    fun onStoreAction(action: StoreActions) {
        when (action) {
            is StoreActions.LoadProducts -> fetchProducts()
            is StoreActions.SelectCategory -> updateCategory(action.category)
            is StoreActions.SetSearchQuery -> updateSearchQuery(action.query)
            is StoreActions.ToggleDropdown -> toggleDropdown(action.expanded)
            is StoreActions.ClearSearchQuery -> clearSearchQuery()
            is StoreActions.DeleteProduct -> deleteProduct(action.pid)
            is StoreActions.ClearValidationError -> clearMessage()
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
            ProductActions.GoBack -> {
            }

            is ProductActions.ProductNameUpdated -> {
                productState = productState.copy(productName = action.productName)
            }

            is ProductActions.PriceUpdated -> {
                productState = productState.copy(price = action.price)
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


                viewModelScope.launch {
                    repository.addProduct(
                        Product(
                            name = productState.productName,
                            price = productState.price.toDouble(),
                            description = productState.description,
                            quantity = productState.quantity.toLong(),
                            weightOrVolume = productState.weight.toFloat(),
                            unit = productState.measurement,
                            category = productState.selectedCategory
                        ),
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

            is ProductActions.CategoryUpdated -> {
                productState = productState.copy(selectedCategory = action.category)
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
                        Product(
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
            productState.description.isBlank() -> "Product description cannot be empty."
            productState.price.toDouble() <= 0 -> "Price must be greater than zero."
            (productState.quantity.toLongOrNull()
                ?: 0L) <= 0 -> "Quantity must be greater than zero."

            (productState.weight.toFloatOrNull() ?: 0f) <= 0 -> "Weight must be greater than zero."
            productState.measurement.isBlank() -> "Measurement unit cannot be empty."
            productState.imageUris.isEmpty() -> "At least one product image is required."
            else -> null
        }
    }
}