package com.aviv.listing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aviv.designsystem.component.EmptyView
import com.aviv.designsystem.component.ErrorView
import com.aviv.designsystem.component.LoadingView
import com.aviv.listing.components.ListingCard
import com.aviv.model.Listing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ListingScreen(
    onListingClick: (Long) -> Unit,
    viewModel: ListingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Discover Your Dream Home",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ListingUiState.Loading -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }
            is ListingUiState.Success -> {
                ListingContent(
                    listings = state.listings,
                    onListingClick = onListingClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is ListingUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.loadListings() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ListingContent(
    listings: List<Listing>,
    onListingClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (listings.isEmpty()) {
        EmptyView(
            message = "No listings available",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = listings,
                key = { it.id }
            ) { listing ->
                ListingCard(
                    listing = listing,
                    onClick = { onListingClick(listing.id) }
                )
            }
        }
    }
}