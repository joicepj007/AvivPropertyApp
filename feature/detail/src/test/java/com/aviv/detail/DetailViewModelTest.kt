package com.aviv.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.aviv.domain.usecase.GetListingDetailUseCase
import com.aviv.model.ListingDetail
import com.aviv.model.OfferType
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getListingDetailUseCase: GetListingDetailUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DetailViewModel

    private val mockDetail = ListingDetail(
        id = 1L,
        bedrooms = 3,
        city = "Berlin",
        area = 150.0,
        imageUrl = "https://example.com/image.jpg",
        price = 500000.0,
        professional = "Real Estate Pro",
        propertyType = "House",
        offerType = OfferType.SALE,
        rooms = 4
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getListingDetailUseCase = mockk()
        savedStateHandle = SavedStateHandle(mapOf("listingId" to 1L))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Arrange
        coEvery { getListingDetailUseCase(1L) } returns flowOf(Result.success(mockDetail))

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)

        // Assert
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState).isInstanceOf(DetailUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init loads listing detail automatically`() = runTest {
        // Arrange
        coEvery { getListingDetailUseCase(1L) } returns flowOf(Result.success(mockDetail))

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(exactly = 1) { getListingDetailUseCase(1L) }
    }

    @Test
    fun `loadListingDetail updates state to Success when use case succeeds`() = runTest {
        // Arrange
        coEvery { getListingDetailUseCase(1L) } returns flowOf(Result.success(mockDetail))

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(DetailUiState.Success::class.java)
            assertThat((state as DetailUiState.Success).detail.id).isEqualTo(1L)
            assertThat(state.detail.city).isEqualTo("Berlin")
            assertThat(state.detail.price).isEqualTo(500000.0)
        }
    }

    @Test
    fun `loadListingDetail updates state to Error when use case fails`() = runTest {
        // Arrange
        val errorMessage = "Failed to load details"
        coEvery { getListingDetailUseCase(1L) } returns flowOf(
            Result.failure(Exception(errorMessage))
        )

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(DetailUiState.Error::class.java)
            assertThat((state as DetailUiState.Error).message).isEqualTo(errorMessage)
        }
    }

    @Test
    fun `loadListingDetail handles null error message`() = runTest {
        // Arrange
        coEvery { getListingDetailUseCase(1L) } returns flowOf(
            Result.failure(Exception())
        )

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(DetailUiState.Error::class.java)
            assertThat((state as DetailUiState.Error).message).isEqualTo("Unknown error occurred")
        }
    }

    @Test
    fun `loadListingDetail can be called multiple times`() = runTest {
        // Arrange
        coEvery { getListingDetailUseCase(1L) } returns flowOf(Result.success(mockDetail))

        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.loadListingDetail()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(exactly = 2) { getListingDetailUseCase(1L) }
    }

    @Test
    fun `uses correct listing id from SavedStateHandle`() = runTest {
        // Arrange
        val listingId = 42L
        savedStateHandle = SavedStateHandle(mapOf("listingId" to listingId))
        coEvery { getListingDetailUseCase(listingId) } returns flowOf(
            Result.success(mockDetail.copy(id = listingId))
        )

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(exactly = 1) { getListingDetailUseCase(listingId) }
    }

    @Test
    fun `state transitions from Loading to Success`() = runTest {
        // Arrange
        coEvery { getListingDetailUseCase(1L) } returns flowOf(Result.success(mockDetail))

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(DetailUiState.Loading::class.java)
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(DetailUiState.Success::class.java)
        }
    }

    @Test
    fun `state transitions from Loading to Error`() = runTest {
        // Arrange
        coEvery { getListingDetailUseCase(1L) } returns flowOf(
            Result.failure(Exception("Error"))
        )

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(DetailUiState.Loading::class.java)
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(DetailUiState.Error::class.java)
        }
    }

    @Test
    fun `detail contains all correct property information`() = runTest {
        // Arrange
        val detailedListing = mockDetail.copy(
            id = 1L,
            city = "Munich",
            price = 750000.0,
            bedrooms = 4,
            rooms = 5,
            area = 200.0,
            propertyType = "Villa",
            professional = "Premium Real Estate"
        )
        coEvery { getListingDetailUseCase(1L) } returns flowOf(Result.success(detailedListing))

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(DetailUiState.Success::class.java)
            val detail = (state as DetailUiState.Success).detail
            assertThat(detail.city).isEqualTo("Munich")
            assertThat(detail.price).isEqualTo(750000.0)
            assertThat(detail.bedrooms).isEqualTo(4)
            assertThat(detail.rooms).isEqualTo(5)
            assertThat(detail.area).isEqualTo(200.0)
            assertThat(detail.propertyType).isEqualTo("Villa")
            assertThat(detail.professional).isEqualTo("Premium Real Estate")
        }
    }

    @Test
    fun `different offer types are handled correctly`() = runTest {
        // Arrange
        val rentDetail = mockDetail.copy(offerType = OfferType.RENT)
        coEvery { getListingDetailUseCase(1L) } returns flowOf(Result.success(rentDetail))

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(DetailUiState.Success::class.java)
            assertThat((state as DetailUiState.Success).detail.offerType).isEqualTo(OfferType.RENT)
        }
    }

    @Test
    fun `network timeout error is handled`() = runTest {
        // Arrange
        val exception = RuntimeException("Network timeout")
        coEvery { getListingDetailUseCase(1L) } returns flowOf(Result.failure(exception))

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(DetailUiState.Error::class.java)
            assertThat((state as DetailUiState.Error).message).isEqualTo("Network timeout")
        }
    }

    @Test
    fun `reload after error shows loading state`() = runTest {
        // Arrange
        coEvery { getListingDetailUseCase(1L) } returns flowOf(
            Result.failure(Exception("Error"))
        )

        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.uiState.test {
            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(DetailUiState.Error::class.java)

            // Reload
            viewModel.loadListingDetail()
            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(DetailUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `different listing ids from SavedStateHandle work correctly`() = runTest {
        // Test with listing ID 999
        savedStateHandle = SavedStateHandle(mapOf("listingId" to 999L))
        val detail999 = mockDetail.copy(id = 999L)
        coEvery { getListingDetailUseCase(999L) } returns flowOf(Result.success(detail999))

        // Act
        viewModel = DetailViewModel(getListingDetailUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(DetailUiState.Success::class.java)
            assertThat((state as DetailUiState.Success).detail.id).isEqualTo(999L)
        }

        verify(exactly = 1) { getListingDetailUseCase(999L) }
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val detail: ListingDetail) : DetailUiState
    data class Error(val message: String) : DetailUiState
}