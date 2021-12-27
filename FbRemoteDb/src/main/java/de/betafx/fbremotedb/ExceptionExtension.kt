package de.betafx.fbremotedb


inline fun <T> runSafeLoggedIn(block: () -> T?): T? {
    return try {
        block.invoke()
    } catch (e: NoUidException) {
        // Do nothing
        null
    }
}