package de.betafx.fbremotedb.remote_db.models

internal data class BucketInternal<T>(
    val name: String,
    val content: List<T>
) {
    fun toMutableMap(): MutableMap<String, Any> = mutableMapOf(
        "content" to content
    )
}

data class Bucket<T>(
    val content: List<T>
) {
    var name: String = ""
}

