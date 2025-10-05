package com.aviv.domain.usecase
import com.aviv.domain.repository.PropertyRepository
import com.aviv.model.ListingDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListingDetailUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(listingId: Long): Flow<Result<ListingDetail>> {
        return repository.getListingDetail(listingId)
    }
}