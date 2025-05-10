package com.appdev.smartkisan.States

import com.appdev.smartkisan.data.Product
import com.appdev.smartkisan.data.statusCard

data class SellerDashboardState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val statusCards: List<statusCard> = emptyList(),
    val errorMessage: String? = null,
    val userImage:String="",
    val userName:String="",
)