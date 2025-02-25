package com.appdev.smartkisan.Actions

sealed interface StoreActions {
    data object LoadProducts : StoreActions
    data class SelectCategory(val category: String) : StoreActions
    data class SetSearchQuery(val query: String) : StoreActions
    data class ToggleDropdown(val expanded: Boolean) : StoreActions
    data object ClearSearchQuery : StoreActions
    data object NavigateToAddProduct : StoreActions
    data object NavigateBack : StoreActions
}