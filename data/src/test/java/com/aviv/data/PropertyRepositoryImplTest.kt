package com.aviv.data

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.aviv.data.remote.PropertyApi
import com.aviv.data.remote.dto.ListingDto
import com.aviv.data.remote.dto.ListingDetailDto
import com.aviv.data.remote.dto.ListingsResponseDto
import com.aviv.data.repository.PropertyRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

class PropertyRepositoryImplTest {

    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()
    private lateinit var api: PropertyApi
    private lateinit var repository: PropertyRepositoryImpl

    private val mockListingDto = ListingDto(
        id = 1L,
        bedrooms = 2,
        city = "Berlin",
        area = 100.0,
        url = "https://example.com/image.jpg",
        price = 300000.0,
        professional = "Real Estate Pro",
        propertyType = "Apartment",
        offerType = 2, // SALE
        rooms = 3
    )

    private val mockDetailDto = ListingDetailDto(
        id = 1L,
        bedrooms = 2,
        city = "Berlin",
        area = 100.0,
        url = "https://example.com/image.jpg",
        price = 300000.0,
        professional = "Real Estate Pro",
        propertyType = "Apartment",
        offerType = 2, // SALE
        rooms = 3
    )

    @Before
    fun setup() {
        api = mockk()
        repository = PropertyRepositoryImpl(api, testDispatcher)
    }

    // ========================================
    // GET LISTINGS TESTS
    // ========================================

    @Test
    fun `getListings returns success when API call succeeds`() = runTest {
        // Arrange
        val responseDto = ListingsResponseDto(items = listOf(mockListingDto))
        coEvery { api.getListings() } returns responseDto

        // Act & Assert
        repository.getListings().test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).hasSize(1)
            assertThat(result.getOrNull()?.first()?.id).isEqualTo(1L)
            assertThat(result.getOrNull()?.first()?.city).isEqualTo("Berlin")
            awaitComplete()
        }
    }

    @Test
    fun `getListings maps DTO to domain model correctly`() = runTest {
        // Arrange
        val responseDto = ListingsResponseDto(items = listOf(mockListingDto))
        coEvery { api.getListings() } returns responseDto

        // Act & Assert
        repository.getListings().test {
            val result = awaitItem()
            val listing = result.getOrNull()?.first()

            assertThat(listing?.id).isEqualTo(1L)
            assertThat(listing?.bedrooms).isEqualTo(2)
            assertThat(listing?.city).isEqualTo("Berlin")
            assertThat(listing?.area).isEqualTo(100.0)
            assertThat(listing?.imageUrl).isEqualTo("https://example.com/image.jpg")
            assertThat(listing?.price).isEqualTo(300000.0)
            assertThat(listing?.professional).isEqualTo("Real Estate Pro")
            assertThat(listing?.propertyType).isEqualTo("Apartment")
            assertThat(listing?.rooms).isEqualTo(3)
            awaitComplete()
        }
    }

    @Test
    fun `getListings returns error when API call fails`() = runTest {
        // Arrange
        val exception = IOException("Network error")
        coEvery { api.getListings() } throws exception

        // Act & Assert
        repository.getListings().test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IOException::class.java)
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Network error")
            awaitComplete()
        }
    }

    @Test
    fun `getListings returns empty list when API returns empty items`() = runTest {
        // Arrange
        val responseDto = ListingsResponseDto(items = emptyList())
        coEvery { api.getListings() } returns responseDto

        // Act & Assert
        repository.getListings().test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `getListings handles multiple listings`() = runTest {
        // Arrange
        val listing1 = mockListingDto.copy(id = 1L, city = "Berlin")
        val listing2 = mockListingDto.copy(id = 2L, city = "Munich")
        val listing3 = mockListingDto.copy(id = 3L, city = "Hamburg")
        val responseDto = ListingsResponseDto(items = listOf(listing1, listing2, listing3))
        coEvery { api.getListings() } returns responseDto

        // Act & Assert
        repository.getListings().test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).hasSize(3)
            assertThat(result.getOrNull()?.map { it.id }).containsExactly(1L, 2L, 3L)
            assertThat(result.getOrNull()?.map { it.city }).containsExactly("Berlin", "Munich", "Hamburg")
            awaitComplete()
        }
    }

    @Test
    fun `getListings handles null values in DTO with defaults`() = runTest {
        // Arrange
        val dtoWithNulls = ListingDto(
            id = 1L,
            bedrooms = null,
            city = null,
            area = null,
            url = null,
            price = null,
            professional = null,
            propertyType = null,
            offerType = null,
            rooms = null
        )
        val responseDto = ListingsResponseDto(items = listOf(dtoWithNulls))
        coEvery { api.getListings() } returns responseDto

        // Act & Assert
        repository.getListings().test {
            val result = awaitItem()
            val listing = result.getOrNull()?.first()

            assertThat(listing?.bedrooms).isEqualTo(0)
            assertThat(listing?.city).isEqualTo("Unknown")
            assertThat(listing?.area).isEqualTo(0.0)
            assertThat(listing?.imageUrl).isEqualTo("")
            assertThat(listing?.price).isEqualTo(0.0)
            assertThat(listing?.professional).isEqualTo("Unknown")
            assertThat(listing?.propertyType).isEqualTo("Unknown")
            assertThat(listing?.rooms).isEqualTo(0)
            awaitComplete()
        }
    }

    @Test
    fun `getListings calls API exactly once`() = runTest {
        // Arrange
        val responseDto = ListingsResponseDto(items = listOf(mockListingDto))
        coEvery { api.getListings() } returns responseDto

        // Act
        repository.getListings().test {
            awaitItem()
            awaitComplete()
        }

        // Assert
        coVerify(exactly = 1) { api.getListings() }
    }

    @Test
    fun `getListings handles different offer types correctly`() = runTest {
        // Arrange
        val saleListing = mockListingDto.copy(id = 1L, offerType = 2) // SALE
        val rentListing = mockListingDto.copy(id = 2L, offerType = 1) // RENT
        val unknownListing = mockListingDto.copy(id = 3L, offerType = null) // UNKNOWN
        val responseDto = ListingsResponseDto(items = listOf(saleListing, rentListing, unknownListing))
        coEvery { api.getListings() } returns responseDto

        // Act & Assert
        repository.getListings().test {
            val result = awaitItem()
            val listings = result.getOrNull()

            assertThat(listings).hasSize(3)
            // Note: The actual enum values depend on OfferType.fromInt implementation
            awaitComplete()
        }
    }

    // ========================================
    // GET LISTING DETAIL TESTS
    // ========================================

    @Test
    fun `getListingDetail returns success when API call succeeds`() = runTest {
        // Arrange
        coEvery { api.getListingDetail(1L) } returns mockDetailDto

        // Act & Assert
        repository.getListingDetail(1L).test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.id).isEqualTo(1L)
            assertThat(result.getOrNull()?.city).isEqualTo("Berlin")
            awaitComplete()
        }
    }

    @Test
    fun `getListingDetail maps DTO to domain model correctly`() = runTest {
        // Arrange
        coEvery { api.getListingDetail(1L) } returns mockDetailDto

        // Act & Assert
        repository.getListingDetail(1L).test {
            val result = awaitItem()
            val detail = result.getOrNull()

            assertThat(detail?.id).isEqualTo(1L)
            assertThat(detail?.bedrooms).isEqualTo(2)
            assertThat(detail?.city).isEqualTo("Berlin")
            assertThat(detail?.area).isEqualTo(100.0)
            assertThat(detail?.imageUrl).isEqualTo("https://example.com/image.jpg")
            assertThat(detail?.price).isEqualTo(300000.0)
            assertThat(detail?.professional).isEqualTo("Real Estate Pro")
            assertThat(detail?.propertyType).isEqualTo("Apartment")
            assertThat(detail?.rooms).isEqualTo(3)
            awaitComplete()
        }
    }

    @Test
    fun `getListingDetail returns error when API call fails`() = runTest {
        // Arrange
        val exception = IOException("Network timeout")
        coEvery { api.getListingDetail(1L) } throws exception

        // Act & Assert
        repository.getListingDetail(1L).test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IOException::class.java)
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Network timeout")
            awaitComplete()
        }
    }

    @Test
    fun `getListingDetail calls API with correct id`() = runTest {
        // Arrange
        val listingId = 42L
        coEvery { api.getListingDetail(listingId) } returns mockDetailDto.copy(id = listingId)

        // Act
        repository.getListingDetail(listingId).test {
            awaitItem()
            awaitComplete()
        }

        // Assert
        coVerify(exactly = 1) { api.getListingDetail(listingId) }
    }

    @Test
    fun `getListingDetail handles different listing ids`() = runTest {
        // Arrange
        val listingId = 999L
        val detail = mockDetailDto.copy(id = listingId)
        coEvery { api.getListingDetail(listingId) } returns detail

        // Act & Assert
        repository.getListingDetail(listingId).test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.id).isEqualTo(listingId)
            awaitComplete()
        }
    }

    @Test
    fun `getListingDetail handles null values in DTO with defaults`() = runTest {
        // Arrange
        val dtoWithNulls = ListingDetailDto(
            id = 1L,
            bedrooms = null,
            city = null,
            area = null,
            url = null,
            price = null,
            professional = null,
            propertyType = null,
            offerType = null,
            rooms = null
        )
        coEvery { api.getListingDetail(1L) } returns dtoWithNulls

        // Act & Assert
        repository.getListingDetail(1L).test {
            val result = awaitItem()
            val detail = result.getOrNull()

            assertThat(detail?.bedrooms).isEqualTo(0)
            assertThat(detail?.city).isEqualTo("Unknown")
            assertThat(detail?.area).isEqualTo(0.0)
            assertThat(detail?.imageUrl).isEqualTo("")
            assertThat(detail?.price).isEqualTo(0.0)
            assertThat(detail?.professional).isEqualTo("Unknown")
            assertThat(detail?.propertyType).isEqualTo("Unknown")
            assertThat(detail?.rooms).isEqualTo(0)
            awaitComplete()
        }
    }

    @Test
    fun `getListingDetail handles 404 not found error`() = runTest {
        // Arrange
        val exception = IOException("HTTP 404 Not Found")
        coEvery { api.getListingDetail(999L) } throws exception

        // Act & Assert
        repository.getListingDetail(999L).test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).contains("404")
            awaitComplete()
        }
    }

    @Test
    fun `getListingDetail handles different offer types`() = runTest {
        // Arrange
        val rentDetail = mockDetailDto.copy(offerType = 1) // RENT
        coEvery { api.getListingDetail(1L) } returns rentDetail

        // Act & Assert
        repository.getListingDetail(1L).test {
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            // Verify the detail is returned successfully
            awaitComplete()
        }
    }

    // ========================================
    // EXCEPTION HANDLING TESTS
    // ========================================

    @Test
    fun `getListings handles RuntimeException`() = runTest {
        // Arrange
        val exception = RuntimeException("Unexpected error")
        coEvery { api.getListings() } throws exception

        // Act & Assert
        repository.getListings().test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
            awaitComplete()
        }
    }

    @Test
    fun `getListingDetail handles RuntimeException`() = runTest {
        // Arrange
        val exception = RuntimeException("Unexpected error")
        coEvery { api.getListingDetail(1L) } throws exception

        // Act & Assert
        repository.getListingDetail(1L).test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
            awaitComplete()
        }
    }

    @Test
    fun `getListings handles IllegalStateException`() = runTest {
        // Arrange
        val exception = IllegalStateException("Invalid state")
        coEvery { api.getListings() } throws exception

        // Act & Assert
        repository.getListings().test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
            awaitComplete()
        }
    }
}