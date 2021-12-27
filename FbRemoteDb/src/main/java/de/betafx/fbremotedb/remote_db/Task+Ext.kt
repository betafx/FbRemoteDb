package de.betafx.fbremotedb.remote_db

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlin.coroutines.Continuation

internal fun <TResult> Task<TResult>.addListener(cons: Continuation<Boolean>) = apply {
    addOnCompleteListener {
        cons.resumeWith(Result.success(true))
    }
    addOnCanceledListener {
        cons.resumeWith(Result.failure(CancellationException()))
    }
    addOnFailureListener {
        cons.resumeWith(Result.success(false))
    }
}
