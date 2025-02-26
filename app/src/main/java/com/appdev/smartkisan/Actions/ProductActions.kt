package com.appdev.smartkisan.Actions

import android.net.Uri

sealed interface ProductActions {
    data class SelectedImageUri(val uri: Uri, val index: Int) : ProductActions
    data class ProductNameUpdated(val productName: String) : ProductActions
    data class PriceUpdated(val price: String) : ProductActions
    data class QuantityUpdated(val quantity: String) : ProductActions
    data class DescriptionUpdated(val description: String) : ProductActions
    data class WeightUpdated(val weight: String) : ProductActions
    data class MeasurementUpdated(val measurement: String) : ProductActions
    data class CategoryUpdated(val category: String) : ProductActions
    data class RemoveImage(val index: Int) : ProductActions
    data class AddToStore(val listOfImageByteArrays: List<ByteArray?>) : ProductActions
    data class UpdateTheProduct(val listOfImageByteArrays: List<ByteArray?>?,val imageUrls:List<String>) : ProductActions
    data object GoBack : ProductActions
    data object ClearValidationError : ProductActions

}