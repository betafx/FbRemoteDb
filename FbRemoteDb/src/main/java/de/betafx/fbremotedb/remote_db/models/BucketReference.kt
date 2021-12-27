package de.betafx.fbremotedb.remote_db.models

internal data class BucketReference(
    val name: String,
    val password: String
) {

    constructor() : this("", "")

    fun toMutableMap() = mapOf(
        "title" to name,
        "password" to password
    )
}