package com.appdev.smartkisan.presentation.feature.Seller.store

import com.appdev.smartkisan.domain.model.Product

data class StoreUiState(
    val products: List<com.appdev.smartkisan.domain.model.Product> = emptyList(),
    val filteredProducts: List<com.appdev.smartkisan.domain.model.Product> = emptyList(),
    val categories: List<String> = listOf("All", "Seeds", "Fertilizers", "Medicine"),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val isDropdownExpanded: Boolean = false
)