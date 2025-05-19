package com.appdev.smartkisan.presentation.feature.farmer.marketplace

import com.appdev.smartkisan.domain.model.Product

sealed interface MarketplaceActions {
    data object LoadProductsForUser : MarketplaceActions
    data class SelectCategory(val category: String) : MarketplaceActions
    data class SetSearchQuery(val query: String) : MarketplaceActions
    data object ClearSearchQuery : MarketplaceActions
    data class NavigateToProductDetail(val product: com.appdev.smartkisan.domain.model.Product) : MarketplaceActions
    data object NavigateBack : MarketplaceActions
    data object ClearValidationError : MarketplaceActions
}