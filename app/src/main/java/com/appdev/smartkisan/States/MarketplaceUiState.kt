package com.appdev.smartkisan.States

import com.appdev.smartkisan.R
import com.appdev.smartkisan.data.Category
import com.appdev.smartkisan.data.Product

data class MarketplaceUiState(
    val categories: List<Category> = listOf(
        Category("All", R.drawable.selectall),
        Category("Seeds", R.drawable.seed),
        Category("Fertilizers", R.drawable.fertlizers),
        Category("Medicine", R.drawable.herbal)
    ),
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
)
