package ui

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface AppRouteFactory {

    fun NavGraphBuilder.create(
        navController: NavController,
        modifier: Modifier,
    )
}

fun NavGraphBuilder.create(
    factories: Set<AppRouteFactory>,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    factories.forEach { factory ->
        with(factory) {
            this@create.create(
                navController = navController,
                modifier = modifier,
            )
        }
    }
}
