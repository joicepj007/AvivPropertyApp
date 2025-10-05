package com.aviv.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aviv.designsystem.component.ErrorView
import com.aviv.designsystem.component.LoadingView
import com.aviv.detail.components.DetailContent
import com.aviv.model.ListingDetail


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailScreen(
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Property Details",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }
            is DetailUiState.Success -> {
                DetailScreenContent(
                    listingDetail = state.listingDetail,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is DetailUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.loadListingDetail() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun DetailScreenContent(
    listingDetail: ListingDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(listingDetail.imageUrl.ifEmpty { null })
                .crossfade(true)
                .build(),
            contentDescription = "Property image",
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(com.aviv.designsystem.R.drawable.ic_no_image),
            error = painterResource(com.aviv.designsystem.R.drawable.ic_no_image),
            fallback = painterResource(com.aviv.designsystem.R.drawable.ic_no_image)
        )

        DetailContent(
            listingDetail = listingDetail,
            modifier = Modifier.padding(20.dp)
        )
    }
}