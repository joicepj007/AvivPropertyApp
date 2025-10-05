package com.aviv.detail.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aviv.detail.DetailScreen
import com.aviv.ui.navigation.NavigationDestination

object DetailDestination : NavigationDestination {
    const val listingIdArg = "listingId"
    override val route = "detail_route/{$listingIdArg}"
    override val destination = "detail_destination"

    fun createNavigationRoute(listingId: Long): String {
        return "detail_route/$listingId"
    }
}

internal class DetailArgs(val listingId: Long) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[DetailDestination.listingIdArg]) as Long)
}

fun NavController.navigateToDetail(listingId: Long) {
    navigate(DetailDestination.createNavigationRoute(listingId))
}

fun NavGraphBuilder.detailScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = DetailDestination.route,
        arguments = listOf(
            navArgument(DetailDestination.listingIdArg) {
                type = NavType.LongType
            }
        )
    ) {
        DetailScreen(onBackClick = onBackClick)
    }
}