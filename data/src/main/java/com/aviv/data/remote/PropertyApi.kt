package com.aviv.data.remote

import com.aviv.data.remote.dto.ListingDetailDto
import com.aviv.data.remote.dto.ListingDto
import com.aviv.data.remote.dto.ListingsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PropertyApi {

    @GET("listings.json")
    suspend fun getListings(): ListingsResponseDto

    @GET("listings/{listingId}.json")
    suspend fun getListingDetail(@Path("listingId") listingId: Long): ListingDetailDto
}
