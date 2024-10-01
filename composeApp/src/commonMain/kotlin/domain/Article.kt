package domain

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: String,
    val title: String,
    val url: String,
    val tags: List<Tag>,
)

@Serializable
data class Tag(val name: String)
