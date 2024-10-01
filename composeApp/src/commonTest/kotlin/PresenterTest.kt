import androidx.compose.runtime.LaunchedEffect
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import domain.Article
import domain.ArticleRepository
import domain.Tag
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import ui.ArticlesPresenter
import ui.ArticlesUiEvent
import ui.ArticlesUiState
import ui.Presenter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class PresenterTest {

    @Test
    fun fetchTriggeredByLaunchedEffect() = runTest {
        val presenter = createPresenter(createRepository())
        val owner = createLifecycleAndViewModelStoreOwner()
        moleculeFlow(RecompositionMode.Immediate) {
            owner.returningCompositionLocalProvider {
                val uiState = presenter.presenter()
                LaunchedEffect(Unit) {
                    when (uiState) {
                        ArticlesUiState.Nothing -> { presenter.eventSink(ArticlesUiEvent.FetchAll) }
                        ArticlesUiState.Loading, is ArticlesUiState.Success -> Unit
                    }
                }
                uiState
            }
        }.test {
            assertTrue { awaitItem() is ArticlesUiState.Nothing }
            assertTrue { awaitItem() is ArticlesUiState.Loading }
            assertTrue { awaitItem() is ArticlesUiState.Success }
        }
    }

    @Test
    fun fetchAll() = runTest {
        val presenter = createPresenter(createRepository())
        val owner = createLifecycleAndViewModelStoreOwner()
        moleculeFlow(RecompositionMode.Immediate) {
            owner.returningCompositionLocalProvider {
                presenter.presenter()
            }
        }.test {
            assertTrue { awaitItem() is ArticlesUiState.Nothing }
            presenter.eventSink(ArticlesUiEvent.FetchAll)
            assertTrue { awaitItem() is ArticlesUiState.Loading }
            assertTrue { awaitItem() is ArticlesUiState.Success }
        }
    }

    @Test
    fun fetchByTag() = runTest {
        val presenter = createPresenter(createRepository(kotlinArticles, javaArticles))
        val owner = createLifecycleAndViewModelStoreOwner()
        moleculeFlow(RecompositionMode.Immediate) {
            owner.returningCompositionLocalProvider {
                presenter.presenter()
            }
        }.test {
            assertTrue { awaitItem() is ArticlesUiState.Nothing }

            presenter.eventSink(ArticlesUiEvent.FetchByTag("kotlin"))
            assertTrue { awaitItem() is ArticlesUiState.Loading }
            val kotlin = awaitItem()
            assertTrue { kotlin is ArticlesUiState.Success }
            kotlin as ArticlesUiState.Success
            assertEquals("kotlin", kotlin.articles.first().tags.first().name.lowercase())

            presenter.eventSink(ArticlesUiEvent.FetchByTag("java"))
            assertTrue { awaitItem() is ArticlesUiState.Loading }
            val java = awaitItem()
            assertTrue { java is ArticlesUiState.Success }
            java as ArticlesUiState.Success
            assertEquals("java", java.articles.first().tags.first().name.lowercase())
        }
    }
}

private val kotlinArticles = List(5) { _ ->
    Article("id", "title", "url", listOf(Tag("kotlin")))
}

private val javaArticles = List(5) { _ ->
    Article("id", "title", "url", listOf(Tag("java")))
}

private fun createRepository(
    kotlinArticles: List<Article> = emptyList(),
    javaArticles: List<Article> = emptyList(),
): ArticleRepository = object : ArticleRepository {

    override suspend fun fetchAll(): List<Article> {
        delay(100.milliseconds)
        return kotlinArticles + javaArticles
    }

    override suspend fun fetchByTag(tag: String): List<Article> {
        delay(100.milliseconds)
        return when (tag) {
            "kotlin" -> kotlinArticles
            "java" -> javaArticles
            else -> kotlinArticles + javaArticles
        }
    }
}

private fun createPresenter(
    repository: ArticleRepository,
): Presenter<ArticlesUiState, ArticlesUiEvent> = ArticlesPresenter(repository)
