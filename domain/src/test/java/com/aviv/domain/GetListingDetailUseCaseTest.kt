package com.aviv.domain

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.aviv.domain.repository.PropertyRepository
import com.aviv.domain.usecase.GetListingDetailUseCase
import com.aviv.model.ListingDetail
import com.aviv.model.OfferType
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetListingDetailUseCaseTest {

    private lateinit var repository: PropertyRepository
    private lateinit var useCase: GetListingDetailUseCase

    private val mockDetail = ListingDetail(
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
        useCase = GetListingDetailUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // Arrange
        coEvery { repository.getListingDetail(1L) } returns flowOf(
            Result.success(mockDetail)
        )

        // Act & Assert
        useCase(1L).test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.id).isEqualTo(1L)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Arrange
        val errorMessage = "Not found"
        val exception = Exception(errorMessage)
        coEvery { repository.getListingDetail(1L) } returns flowOf(
            Result.failure(exception)
        )

        // Act & Assert
        useCase(1L).test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo(errorMessage)
            awaitComplete()
        }
    }

    @Test
    fun `invoke delegates to repository with correct id`() = runTest {
        // Arrange
        val listingId = 123L
        coEvery { repository.getListingDetail(listingId) } returns flowOf(
            Result.success(mockDetail.copy(id = listingId))
        )

        // Act
        useCase(listingId).test {
            awaitItem()
            awaitComplete()
        }

        // Assert
        verify(exactly = 1) { repository.getListingDetail(listingId) }
    }

    @Test
    fun `invoke with different ids calls repository with correct id`() = runTest {
        // Arrange
        coEvery { repository.getListingDetail(any()) } returns flowOf(
            Result.success(mockDetail)
        )

        // Act
        useCase(999L).test {
            awaitItem()
            awaitComplete()
        }

        // Assert
        verify(exactly = 1) { repository.getListingDetail(999L) }
    }

    @Test
    fun `invoke handles network error`() = runTest {
        // Arrange
        val exception = RuntimeException("Network timeout")
        coEvery { repository.getListingDetail(1L) } returns flowOf(
            Result.failure(exception)
        )

        // Act & Assert
        useCase(1L).test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Network timeout")
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns correct listing detail data`() = runTest {
        // Arrange
        val detailedListing = mockDetail.copy(
            id = 42L,
            city = "Munich",
            price = 750000.0,
            bedrooms = 4,
            rooms = 5
        )
        coEvery { repository.getListingDetail(42L) } returns flowOf(
            Result.success(detailedListing)
        )

        // Act & Assert
        useCase(42L).test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            val detail = result.getOrNull()
            assertThat(detail?.id).isEqualTo(42L)
            assertThat(detail?.city).isEqualTo("Munich")
            assertThat(detail?.price).isEqualTo(750000.0)
            assertThat(detail?.bedrooms).isEqualTo(4)
            assertThat(detail?.rooms).isEqualTo(5)
            awaitComplete()
        }
    }

    @Test
    fun `invoke propagates different offer types correctly`() = runTest {
        // Arrange
        val rentDetail = mockDetail.copy(offerType = OfferType.RENT)
        coEvery { repository.getListingDetail(1L) } returns flowOf(
            Result.success(rentDetail)
        )

        // Act & Assert
        useCase(1L).test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.offerType).isEqualTo(OfferType.RENT)
            awaitComplete()
        }
    }
}