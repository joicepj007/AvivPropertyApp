package com.aviv.listing.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.aviv.listing.ListingScreen
import com.aviv.ui.navigation.NavigationDestination

object ListingDestination : NavigationDestination {
    override val route = "listing_route"
    override val destination = "listing_destination"
}

fun NavController.navigateToListing(navOptions: NavOptions? = null) {
    navigate(ListingDestination.route, navOptions)
}

fun NavGraphBuilder.listingScreen(
    onListingClick: (Long) -> Unit
) {
    composable(route = ListingDestination.route) {
        ListingScreen(onListingClick = onListingClick)
    }
}