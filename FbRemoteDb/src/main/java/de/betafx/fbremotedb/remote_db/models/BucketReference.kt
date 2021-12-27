package de.betafx.fbremotedb.remote_db.models

import androidx.annotation.Keep

internal data class BucketReference(
    val name: String,
    val password: String
) {

    @Keep
    constructor() : this("", "")

    fun toMutableMap() = mapOf(
        "title" to name,
        "password" to password
    )
}