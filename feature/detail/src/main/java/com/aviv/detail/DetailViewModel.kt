package com.aviv.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aviv.detail.navigation.DetailArgs
import com.aviv.domain.usecase.GetListingDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getListingDetailUseCase: GetListingDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val detailArgs = DetailArgs(savedStateHandle)

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadListingDetail()
    }

    fun loadListingDetail() {
        viewModelScope.launch {
            _uiState.update { DetailUiState.Loading }

            getListingDetailUseCase(detailArgs.listingId).collect { result ->
                _uiState.update {
                    result.fold(
                        onSuccess = { detail -> DetailUiState.Success(detail) },
                        onFailure = { error ->
                            DetailUiState.Error(error.message ?: "Unknown error occurred")
                        }
                    )
                }
            }
        }
    }
}