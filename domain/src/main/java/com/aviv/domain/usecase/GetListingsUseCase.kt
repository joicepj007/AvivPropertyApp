package com.aviv.domain.usecase
import com.aviv.domain.repository.PropertyRepository
import com.aviv.model.Listing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListingsUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    operator fun invoke(): Flow<Result<List<Listing>>> {
        return repository.getListings()
    }
}