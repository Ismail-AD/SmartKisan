package com.appdev.smartkisan.Actions

import com.appdev.smartkisan.data.Product

sealed interface SellerStoreActions {
    data object LoadProductsForAdmin : SellerStoreActions
    data class SelectCategory(val category: String) : SellerStoreActions
    data class SetSearchQuery(val query: String) : SellerStoreActions
    data class ToggleDropdown(val expanded: Boolean) : SellerStoreActions
    data object ClearSearchQuery : SellerStoreActions
    data class NavigateToAddProduct(val product: Product?) : SellerStoreActions
    data class NavigateToProductDetail(val product: Product) : SellerStoreActions
    data object NavigateBack : SellerStoreActions
    data class DeleteProduct(val pid:Long) : SellerStoreActions
    data object ClearValidationError : SellerStoreActions

}