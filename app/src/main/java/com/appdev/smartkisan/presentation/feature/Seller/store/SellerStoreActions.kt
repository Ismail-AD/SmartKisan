package com.appdev.smartkisan.presentation.feature.Seller.store

import com.appdev.smartkisan.domain.model.Product

sealed interface SellerStoreActions {
    data object LoadProductsForAdmin : SellerStoreActions
    data class SelectCategory(val category: String) : SellerStoreActions
    data class SetSearchQuery(val query: String) : SellerStoreActions
    data class ToggleDropdown(val expanded: Boolean) : SellerStoreActions
    data object ClearSearchQuery : SellerStoreActions
    data class NavigateToAddProduct(val product: com.appdev.smartkisan.domain.model.Product?) : SellerStoreActions
    data class NavigateToProductDetail(val product: com.appdev.smartkisan.domain.model.Product) : SellerStoreActions
    data object NavigateBack : SellerStoreActions
    data class DeleteProduct(val pid:Long) : SellerStoreActions
    data object ClearValidationError : SellerStoreActions

}