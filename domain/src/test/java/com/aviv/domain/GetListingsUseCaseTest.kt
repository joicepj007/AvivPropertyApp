package com.aviv.domain
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.aviv.domain.repository.PropertyRepository
import com.aviv.domain.usecase.GetListingsUseCase
import com.aviv.model.Listing
import com.aviv.model.OfferType
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetListingsUseCaseTest {

    private lateinit var repository: PropertyRepository
    private lateinit var useCase: GetListingsUseCase

    private val mockListing = Listing(
        id = 1L,
        bedrooms = 2,
        city = "Berlin",
        area = 100.0,
        imageUrl = "image.jpg",
        price = 300000.0,
        professional = "Real Estate Pro",
        propertyType = "Apartment",
        offerType = OfferType.SALE,
        rooms = 3
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetListingsUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // Arrange
        val mockListings = listOf(mockListing)
        coEvery { repository.getListings() } returns flowOf(
            Result.success(mockListings)
        )

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).hasSize(1)
            assertThat(result.getOrNull()?.first()?.id).isEqualTo(1L)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        val exception = Exception(errorMessage)
        coEvery { repository.getListings() } returns flowOf(
            Result.failure(exception)
        )

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo(errorMessage)
            awaitComplete()
        }
    }

    @Test
    fun `invoke delegates to repository`() = runTest {
        // Arrange
        coEvery { repository.getListings() } returns flowOf(
            Result.success(emptyList())
        )

        // Act
        useCase().test {
            awaitItem()
            awaitComplete()
        }

        // Assert
        verify(exactly = 1) { repository.getListings() }
    }

    @Test
    fun `invoke handles empty list`() = runTest {
        // Arrange
        coEvery { repository.getListings() } returns flowOf(
            Result.success(emptyList())
        )

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles multiple listings`() = runTest {
        // Arrange
        val listing1 = mockListing.copy(id = 1L)
        val listing2 = mockListing.copy(id = 2L)
        val listing3 = mockListing.copy(id = 3L)
        val mockListings = listOf(listing1, listing2, listing3)

        coEvery { repository.getListings() } returns flowOf(
            Result.success(mockListings)
        )

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).hasSize(3)
            assertThat(result.getOrNull()?.map { it.id }).containsExactly(1L, 2L, 3L)
            awaitComplete()
        }
    }

    @Test
    fun `invoke propagates exception from repository`() = runTest {
        // Arrange
        val exception = RuntimeException("Database error")
        coEvery { repository.getListings() } returns flowOf(
            Result.failure(exception)
        )

        // Act & Assert
        useCase().test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
            awaitComplete()
        }
    }
}
