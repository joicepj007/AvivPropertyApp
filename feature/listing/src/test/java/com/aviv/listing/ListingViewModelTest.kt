package com.aviv.listing

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.aviv.domain.usecase.GetListingsUseCase
import com.aviv.model.Listing
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
class ListingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getListingsUseCase: GetListingsUseCase
    private lateinit var viewModel: ListingViewModel

    private val mockListing = Listing(
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
        getListingsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Arrange
        coEvery { getListingsUseCase() } returns flowOf(Result.success(emptyList()))

        // Act
        viewModel = ListingViewModel(getListingsUseCase)

        // Assert
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState).isInstanceOf(ListingUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init loads listings automatically`() = runTest {
        // Arrange
        val mockListings = listOf(mockListing)
        coEvery { getListingsUseCase() } returns flowOf(Result.success(mockListings))

        // Act
        viewModel = ListingViewModel(getListingsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(exactly = 1) { getListingsUseCase() }
    }

    @Test
    fun `loadListings updates state to Success when use case succeeds`() = runTest {
        // Arrange
        val mockListings = listOf(mockListing)
        coEvery { getListingsUseCase() } returns flowOf(Result.success(mockListings))

        // Act
        viewModel = ListingViewModel(getListingsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ListingUiState.Success::class.java)
            assertThat((state as ListingUiState.Success).listings).hasSize(1)
            assertThat(state.listings.first().id).isEqualTo(1L)
            assertThat(state.listings.first().city).isEqualTo("Berlin")
        }
    }

    @Test
    fun `loadListings updates state to Error when use case fails`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        coEvery { getListingsUseCase() } returns flowOf(
            Result.failure(Exception(errorMessage))
        )

        // Act
        viewModel = ListingViewModel(getListingsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ListingUiState.Error::class.java)
            assertThat((state as ListingUiState.Error).message).isEqualTo(errorMessage)
        }
    }

    @Test
    fun `loadListings handles null error message`() = runTest {
        // Arrange
        coEvery { getListingsUseCase() } returns flowOf(
            Result.failure(Exception())
        )

        // Act
        viewModel = ListingViewModel(getListingsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ListingUiState.Error::class.java)
            assertThat((state as ListingUiState.Error).message).isEqualTo("Unknown error occurred")
        }
    }

    @Test
    fun `loadListings can be called multiple times`() = runTest {
        // Arrange
        val mockListings = listOf(mockListing)
        coEvery { getListingsUseCase() } returns flowOf(Result.success(mockListings))

        viewModel = ListingViewModel(getListingsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.loadListings()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(exactly = 2) { getListingsUseCase() }
    }

    @Test
    fun `empty list shows success state with empty list`() = runTest {
        // Arrange
        coEvery { getListingsUseCase() } returns flowOf(Result.success(emptyList()))

        // Act
        viewModel = ListingViewModel(getListingsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ListingUiState.Success::class.java)
            assertThat((state as ListingUiState.Success).listings).isEmpty()
        }
    }

    @Test
    fun `multiple listings are handled correctly`() = runTest {
        // Arrange
        val listing1 = mockListing.copy(id = 1L, city = "Berlin")
        val listing2 = mockListing.copy(id = 2L, city = "Munich")
        val listing3 = mockListing.copy(id = 3L, city = "Hamburg")
        val mockListings = listOf(listing1, listing2, listing3)

        coEvery { getListingsUseCase() } returns flowOf(Result.success(mockListings))

        // Act
        viewModel = ListingViewModel(getListingsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ListingUiState.Success::class.java)
            assertThat((state as ListingUiState.Success).listings).hasSize(3)
            assertThat(state.listings.map { it.id }).containsExactly(1L, 2L, 3L)
        }
    }

    @Test
    fun `state transitions from Loading to Success`() = runTest {
        // Arrange
        val mockListings = listOf(mockListing)
        coEvery { getListingsUseCase() } returns flowOf(Result.success(mockListings))

        // Act
        viewModel = ListingViewModel(getListingsUseCase)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ListingUiState.Loading::class.java)
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(ListingUiState.Success::class.java)
        }
    }

    @Test
    fun `state transitions from Loading to Error`() = runTest {
        // Arrange
        coEvery { getListingsUseCase() } returns flowOf(
            Result.failure(Exception("Error"))
        )

        // Act
        viewModel = ListingViewModel(getListingsUseCase)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ListingUiState.Loading::class.java)
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(ListingUiState.Error::class.java)
        }
    }

    @Test
    fun `different offer types are handled correctly`() = runTest {
        // Arrange
        val saleListing = mockListing.copy(id = 1L, offerType = OfferType.SALE)
        val rentListing = mockListing.copy(id = 2L, offerType = OfferType.RENT)
        val unknownListing = mockListing.copy(id = 3L, offerType = OfferType.UNKNOWN)
        val mockListings = listOf(saleListing, rentListing, unknownListing)

        coEvery { getListingsUseCase() } returns flowOf(Result.success(mockListings))

        // Act
        viewModel = ListingViewModel(getListingsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ListingUiState.Success::class.java)
            val listings = (state as ListingUiState.Success).listings
            assertThat(listings[0].offerType).isEqualTo(OfferType.SALE)
            assertThat(listings[1].offerType).isEqualTo(OfferType.RENT)
            assertThat(listings[2].offerType).isEqualTo(OfferType.UNKNOWN)
        }
    }
}