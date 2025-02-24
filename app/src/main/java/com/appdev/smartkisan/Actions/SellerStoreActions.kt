package com.appdev.smartkisan.Actions

import android.net.Uri

sealed interface SellerStoreActions {
    data class SelectedImageUri(val uri: Uri, val index: Int) : SellerStoreActions
    data class ProductNameUpdated(val productName: String) : SellerStoreActions
    data class PriceUpdated(val price: Double) : SellerStoreActions
    data class QuantityUpdated(val quantity: Long) : SellerStoreActions
    data class DescriptionUpdated(val description: String) : SellerStoreActions
    data class WeightUpdated(val weight: Float) : SellerStoreActions
    data class MeasurementUpdated(val measurement: String) : SellerStoreActions
    data class AddToStore(val listOfImageByteArrays: List<ByteArray?>) : SellerStoreActions
    data object GoBack : SellerStoreActions
}