package com.appdev.smartkisan.Actions

import android.net.Uri

sealed interface ProductActions {
    object GoBack : ProductActions
    object ClearValidationError : ProductActions

    data class ProductNameUpdated(val productName: String) : ProductActions
    data class PriceUpdated(val price: String) : ProductActions
    data class BrandUpdated(val brand: String) : ProductActions
    data class DescriptionUpdated(val description: String) : ProductActions
    data class WeightUpdated(val weight: String) : ProductActions
    data class MeasurementUpdated(val measurement: String) : ProductActions
    data class QuantityUpdated(val quantity: String) : ProductActions
    data class CategoryUpdated(val category: String) : ProductActions

    data class ApplicationMethodUpdated(val method: String) : ProductActions
    data class PlantingSeasonUpdated(val season: String) : ProductActions

    // Disease management for Medicine category
    data class AddDisease(val disease: String = "") : ProductActions
    data class UpdateDisease(val index: Int, val disease: String) : ProductActions
    data class RemoveDisease(val index: Int) : ProductActions

    data class SelectedImageUri(val uri: Uri, val index: Int) : ProductActions
    data class RemoveImage(val index: Int) : ProductActions

    data class AddToStore(val listOfImageByteArrays: List<ByteArray?>) : ProductActions
    data class UpdateTheProduct(
        val listOfImageByteArrays: List<ByteArray?>?,
        val imageUrls: List<String>
    ) : ProductActions
}