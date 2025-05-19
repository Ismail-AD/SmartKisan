package com.appdev.smartkisan.presentation.feature.farmer.marketplace

import com.appdev.smartkisan.R
import com.appdev.smartkisan.domain.model.Category
import com.appdev.smartkisan.domain.model.Product

data class MarketplaceUiState(
    val categories: List<Category> = listOf(
        Category("All", R.drawable.selectall),
        Category("Seeds", R.drawable.seed),
        Category("Fertilizers", R.drawable.fertlizers),
        Category("Medicine", R.drawable.herbal)
    ),
    val products: List<com.appdev.smartkisan.domain.model.Product> = emptyList(),
    val filteredProducts: List<com.appdev.smartkisan.domain.model.Product> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
)
