package com.aviv.model

data class Listing(
    val id: Long,
    val bedrooms: Int,
    val city: String,
    val area: Double,
    val imageUrl: String,
    val price: Double,
    val professional: String,
    val propertyType: String,
    val offerType: OfferType,
    val rooms: Int
)