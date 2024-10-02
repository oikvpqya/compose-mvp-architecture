package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject

@Serializable
data class Sub(
    val tag: String,
)

@Inject
class SubRouteFactory(
    private val presenterFactory: () -> ArticlesPresenter,
) : AppRouteFactory {

    override fun NavGraphBuilder.create(navController: NavController, modifier: Modifier) {
        composable<Sub> { entry ->
            val tag = entry.toRoute<Sub>().tag
            val presenter = remember { presenterFactory() }
            val uiState = presenter.presenter()

            SubScreen(
                uiState = uiState,
                modifier = modifier,
                onPopUp = { navController.popBackStack() },
            )

            LaunchedEffect(Unit) {
                when (uiState) {
                    ArticlesUiState.Nothing -> {
                        presenter.eventSink(ArticlesUiEvent.FetchByTag(tag))
                    }
                    ArticlesUiState.Loading, is ArticlesUiState.Success -> Unit
                }
            }
        }
    }
}

@Composable
fun SubScreen(
    uiState: ArticlesUiState,
    modifier: Modifier = Modifier,
    onPopUp: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Kotlin Articles") },
                navigationIcon = {
                    IconButton( onClick = onPopUp ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            ArticlesUiState.Loading -> { Text("Loading") }
            ArticlesUiState.Nothing -> { Text("Nothing") }
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
