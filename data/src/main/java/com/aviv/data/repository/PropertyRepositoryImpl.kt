package com.aviv.data.repository

import com.aviv.common.di.IoDispatcher
import com.aviv.data.mapper.ListingMapper.toDomain
import com.aviv.data.remote.PropertyApi
import com.aviv.domain.repository.PropertyRepository
import com.aviv.model.Listing
import com.aviv.model.ListingDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor(
    private val api: PropertyApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PropertyRepository {

    override fun getListings(): Flow<Result<List<Listing>>> = flow {
        try {
            val response = api.getListings()
            val listings = response.items.map { it.toDomain() }  // ✅ Extract items
            emit(Result.success(listings))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(ioDispatcher)

    override fun getListingDetail(listingId: Long): Flow<Result<ListingDetail>> = flow {
        try {
            val detail = api.getListingDetail(listingId)
            emit(Result.success(detail.toDomain()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(ioDispatcher)
}
