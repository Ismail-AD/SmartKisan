package com.appdev.smartkisan.presentation.feature.Seller.home

import com.appdev.smartkisan.domain.model.Product
import com.appdev.smartkisan.domain.model.statusCard

data class SellerHomeState(
    val isLoading: Boolean = false,
    val products: List<com.appdev.smartkisan.domain.model.Product> = emptyList(),
    val statusCards: List<com.appdev.smartkisan.domain.model.statusCard> = emptyList(),
    val errorMessage: String? = null,
    val userImage:String="",
    val userName:String="",
)