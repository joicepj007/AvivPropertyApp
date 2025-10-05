package com.aviv.domain.repository

import com.aviv.model.Listing
import com.aviv.model.ListingDetail
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getListings(): Flow<Result<List<Listing>>>
    fun getListingDetail(listingId: Long): Flow<Result<ListingDetail>>
}