package com.aviv.data.remote.dto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListingsResponseDto(
    @SerialName("items") val items: List<ListingDto>
)
