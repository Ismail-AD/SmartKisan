package com.appdev.smartkisan.presentation.feature.farmer.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.data.repository.Repository
import com.appdev.smartkisan.Utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    val repository: Repository,
) : ViewModel() {
    private val _marketplaceUiState = MutableStateFlow(MarketplaceUiState())
    val marketplaceUiState = _marketplaceUiState.asStateFlow()

    private fun fetchProductsForUser() {
        viewModelScope.launch {
            _marketplaceUiState.value =
                _marketplaceUiState.value.copy(isLoading = true, error = null)

            repository.getAllProducts().collect { result ->
                _marketplaceUiState.value = when (result) {
                    is ResultState.Success -> {
                        _marketplaceUiState.value.copy(
                            products = result.data,
                            filteredProducts = result.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is ResultState.Failure -> {
                        _marketplaceUiState.value.copy(
                            isLoading = false,
                            error = result.msg.localizedMessage
                        )
                    }

                    ResultState.Loading -> {
                        _marketplaceUiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }
    }

    fun onMarketplaceAction(action: MarketplaceActions) {
        when (action) {
            is MarketplaceActions.LoadProductsForUser -> {
                fetchProductsForUser()
            }

            is MarketplaceActions.SelectCategory -> {
                selectCategory(action.category)
            }


            is MarketplaceActions.ClearValidationError -> {
                clearMessage()
            }

            else -> {}
        }
    }

    private fun clearMessage() {
        _marketplaceUiState.value = _marketplaceUiState.value.copy(
            error = null
        )
    }

    private fun selectCategory(category: String) {
        val filteredProducts = _marketplaceUiState.value.products.filter { product ->
            product.category.contains(category, ignoreCase = true) || category == "All"
        }
        _marketplaceUiState.value =
            _marketplaceUiState.value.copy(filteredProducts = filteredProducts, selectedCategory = category)
    }


}