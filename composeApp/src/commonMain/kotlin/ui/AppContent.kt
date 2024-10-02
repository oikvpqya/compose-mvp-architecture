package ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.tatarka.inject.annotations.Inject

interface AppContent {

    @Composable
    fun Content(
        modifier: Modifier,
    )
}

@Inject
class DefaultAppContent(
    private val routeFactories: Set<AppRouteFactory>,
) : AppContent {

    @Composable
    override fun Content(
        modifier: Modifier,
    ) {
        MaterialTheme {
            Surface {
                App(
                    routeFactories = routeFactories,
                    modifier = modifier,
                )
            }
        }
    }
}

@Composable
fun App(
    routeFactories: Set<AppRouteFactory>,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Main,
        modifier = modifier,
    ) {
        create(
            factories = routeFactories,
            navController = navController,
        )
    }
}
