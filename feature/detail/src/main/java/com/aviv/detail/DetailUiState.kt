package com.aviv.detail

import com.aviv.model.ListingDetail

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val listingDetail: ListingDetail) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
