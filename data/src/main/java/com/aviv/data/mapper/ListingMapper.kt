package com.aviv.data.mapper

import com.aviv.data.remote.dto.ListingDetailDto
import com.aviv.data.remote.dto.ListingDto
import com.aviv.model.Listing
import com.aviv.model.ListingDetail
import com.aviv.model.OfferType


object ListingMapper {

    fun ListingDto.toDomain(): Listing {
        return Listing(
            id = id,
            bedrooms = bedrooms ?: 0,
            city = city ?: "Unknown",
            area = area ?: 0.0,
            imageUrl = url ?: "",
            price = price ?: 0.0,
            professional = professional ?: "Unknown",
            propertyType = propertyType ?: "Unknown",
            offerType = OfferType.fromInt(offerType),
            rooms = rooms ?: 0
        )
    }

    fun ListingDetailDto.toDomain(): ListingDetail {
        return ListingDetail(
            id = id,
            bedrooms = bedrooms ?: 0,
            city = city ?: "Unknown",
            area = area ?: 0.0,
            imageUrl = url ?: "",
            price = price ?: 0.0,
            professional = professional ?: "Unknown",
            propertyType = propertyType ?: "Unknown",
            offerType = OfferType.fromInt(offerType),
            rooms = rooms ?: 0
        )
    }
}