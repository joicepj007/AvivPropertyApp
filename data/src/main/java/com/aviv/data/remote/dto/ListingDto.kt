package com.aviv.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListingDto(
    @SerialName("id") val id: Long,
    @SerialName("bedrooms") val bedrooms: Int? = null,
    @SerialName("city") val city: String? = null,
    @SerialName("area") val area: Double? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("professional") val professional: String? = null,
    @SerialName("propertyType") val propertyType: String? = null,
    @SerialName("offerType") val offerType: Int? = null,
    @SerialName("rooms") val rooms: Int? = null
)