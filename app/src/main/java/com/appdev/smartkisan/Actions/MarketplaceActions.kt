package com.appdev.smartkisan.Actions

import com.appdev.smartkisan.data.Product

sealed interface MarketplaceActions {
    data object LoadProductsForUser : MarketplaceActions
    data class SelectCategory(val category: String) : MarketplaceActions
    data class SetSearchQuery(val query: String) : MarketplaceActions
    data object ClearSearchQuery : MarketplaceActions
    data class NavigateToProductDetail(val product: Product) : MarketplaceActions
    data object NavigateBack : MarketplaceActions
    data object ClearValidationError : MarketplaceActions
}