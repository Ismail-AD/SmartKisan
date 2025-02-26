package com.appdev.smartkisan.Actions

import com.appdev.smartkisan.data.Product

sealed interface StoreActions {
    data object LoadProducts : StoreActions
    data class SelectCategory(val category: String) : StoreActions
    data class SetSearchQuery(val query: String) : StoreActions
    data class ToggleDropdown(val expanded: Boolean) : StoreActions
    data object ClearSearchQuery : StoreActions
    data class NavigateToAddProduct(val product: Product?) : StoreActions
    data class NavigateToProductDetail(val product: Product) : StoreActions
    data object NavigateBack : StoreActions
    data class DeleteProduct(val pid:Long) : StoreActions
    data object ClearValidationError : StoreActions

}