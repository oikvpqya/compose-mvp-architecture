package domain

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

interface ArticleRepository {

    suspend fun fetchAll() : List<Article>
    suspend fun fetchByTag(tag: String) : List<Article>
}

@Inject
class DefaultArticleRepository(
    private val httpClient: HttpClient,
) : ArticleRepository {

    private suspend fun fetch(target: String): List<Article> {
        val result = httpClient.get("https://qiita.com/$target")
        val json = Json {
            ignoreUnknownKeys = true
        }
        return json.decodeFromString<List<Article>>(result.bodyAsText())
    }

    override suspend fun fetchAll(): List<Article> {
        return fetch("api/v2/items")
    }

    override suspend fun fetchByTag(tag: String): List<Article> {
        return fetch("api/v2/tags/$tag/items")
    }
}
