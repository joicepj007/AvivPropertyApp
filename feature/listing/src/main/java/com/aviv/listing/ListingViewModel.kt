package com.aviv.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aviv.domain.usecase.GetListingsUseCase
import com.aviv.model.Listing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ListingUiState {
    data object Loading : ListingUiState
    data class Success(val listings: List<Listing>) : ListingUiState
    data class Error(val message: String) : ListingUiState
}

@HiltViewModel
class ListingViewModel @Inject constructor(
    private val getListingsUseCase: GetListingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListingUiState>(ListingUiState.Loading)
    val uiState: StateFlow<ListingUiState> = _uiState.asStateFlow()

    init {
        loadListings()
    }

    fun loadListings() {
        viewModelScope.launch {
            _uiState.update { ListingUiState.Loading }

            getListingsUseCase().collect { result ->
                _uiState.update {
                    result.fold(
                        onSuccess = { listings -> ListingUiState.Success(listings) },
                        onFailure = { error ->
                            ListingUiState.Error(error.message ?: "Unknown error occurred")
                        }
                    )
                }
            }
        }
    }
}