package com.appdev.smartkisan.data

data class Product(
    var id: Long,
    var name: String,
    var price: Long,
    var discountPrice: Long = 0L,
    var image: Int,
    var ratings: Float,
    var reviewsCount: Long,
    var description: String,
    var quantity: Float,
    var unit: String? = ""
)
