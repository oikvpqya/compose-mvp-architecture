package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject

@Serializable
object Main

@Inject
class MainRouteFactory(
    private val presenterFactory: () -> ArticlesPresenter,
) : AppRouteFactory {

    override fun NavGraphBuilder.create(navController: NavController, modifier: Modifier) {
        composable<Main> { _ ->
            val presenter = remember { presenterFactory() }
            val uiState = presenter.presenter()

            MainScreen(
                uiState = uiState,
                modifier = modifier,
                onNavigateToSub = { navController.navigate(Sub("kotlin")) },
            )

            LaunchedEffect(Unit) {
                when (uiState) {
                    ArticlesUiState.Nothing -> {
                        presenter.eventSink(ArticlesUiEvent.FetchAll)
                    }
                    ArticlesUiState.Loading, is ArticlesUiState.Success -> Unit
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    uiState: ArticlesUiState,
    modifier: Modifier = Modifier,
    onNavigateToSub: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Qiita Articles") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSub,
            ) {
                Text("Kotlin Articles")
            }
        }
    ) { innerPadding ->
        when (uiState) {
            ArticlesUiState.Loading -> {
                Text("Loading")
            }

            ArticlesUiState.Nothing -> {
                Text("Nothing")
            }

            is ArticlesUiState.Success -> {
                Column(
                    modifier = Modifier.padding(innerPadding),
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(uiState.articles) { article ->
                            Card { Text(text = article.title) }
                        }
                    }
                }
            }
        }
    }
}
