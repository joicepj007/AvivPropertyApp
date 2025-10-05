package com.aviv.property

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.aviv.detail.navigation.detailScreen
import com.aviv.detail.navigation.navigateToDetail
import com.aviv.listing.navigation.ListingDestination
import com.aviv.listing.navigation.listingScreen


@Composable
fun PropertyNavHost(
    modifier: Modifier = Modifier,
    startDestination: String = ListingDestination.route
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        listingScreen(
            onListingClick = { listingId ->
                navController.navigateToDetail(listingId)
            }
        )

        detailScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}