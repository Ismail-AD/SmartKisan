package com.appdev.smartkisan.States

import com.appdev.smartkisan.data.Product

data class StoreUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val categories: List<String> = listOf("All", "Seeds", "Fertilizers", "Medicine"),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val isDropdownExpanded: Boolean = false
)