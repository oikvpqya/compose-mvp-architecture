package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import domain.Article
import domain.ArticleRepository
import io.github.takahirom.rin.RetainedObserver
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

sealed interface ArticlesUiState {

    data object Nothing : ArticlesUiState

    data object Loading : ArticlesUiState

    data class Success(
        val articles: List<Article>
    ) : ArticlesUiState
}

sealed interface ArticlesUiEvent {

    data object FetchAll : ArticlesUiEvent

    data class FetchByTag(
        val tag: String,
    ) : ArticlesUiEvent
}

interface Presenter<STATE, EVENT> {

    @Composable
    fun presenter(): STATE

    fun eventSink(event: EVENT)
}

@Inject
class ArticlesPresenter(
    private val repository: ArticleRepository,
) : Presenter<ArticlesUiState, ArticlesUiEvent> {

    private val eventFlow = MutableSharedFlow<ArticlesUiEvent>(extraBufferCapacity = 20)

    @Composable
    override fun presenter(): ArticlesUiState {
        val coroutineScope = rememberRetained {
            object : RetainedObserver {
                val coroutineScope = CoroutineScope(Dispatchers.Default)

                override fun onForgotten() {
                    coroutineScope.cancel()
                }

                override fun onRemembered() = Unit
            }
        }.coroutineScope

        var uiState by rememberRetained { mutableStateOf<ArticlesUiState>(ArticlesUiState.Nothing) }

        fun eventSink(event: ArticlesUiEvent) {
            when (event) {
                ArticlesUiEvent.FetchAll -> {
                    coroutineScope.launch {
                        uiState = ArticlesUiState.Loading
                        uiState = ArticlesUiState.Success(repository.fetchAll())
                    }
                }

                is ArticlesUiEvent.FetchByTag -> {
                    coroutineScope.launch {
                        uiState = ArticlesUiState.Loading
                        uiState = ArticlesUiState.Success(repository.fetchByTag(event.tag))
                    }
                }
            }
        }

        LaunchedEffect(eventFlow) {
            eventFlow.collect { event ->
                launch {
                    eventSink(event)
                }
            }
        }

        return uiState
    }

    override fun eventSink(event: ArticlesUiEvent) {
        eventFlow.tryEmit(event)
    }
}
