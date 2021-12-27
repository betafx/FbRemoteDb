package de.betafx.fbremotedb.remote_db

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.betafx.fbremotedb.NoUidException
import de.betafx.fbremotedb.remote_db.models.BucketReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class RemoteUserBuckets(private val version: String) {

    private val database = Firebase.database

    private val userDatabase
        get() = database
            .getReference("user")
            .child(
                FirebaseAuth.getInstance().uid ?: throw NoUidException()
            )

    private val userBucketDatabase = userDatabase
        .child("buckets")
        .child(version)

    @Throws(NoUidException::class)
    suspend fun getPrivateBucketReferences(): List<BucketReference> =
        suspendCancellableCoroutine { cons ->

            val callback = ValueListenerList(
                onFinished = { list: List<BucketReference> -> cons.resume(list) },
                onCancel = { cons.cancel(CancellationException()) },
                clazz = BucketReference::class.java
            )

            userBucketDatabase.addListenerForSingleValueEvent(callback)
        }

    @Throws(NoUidException::class)
    suspend fun storePrivateBucketReference(bucketReference: BucketReference): Boolean =
        suspendCoroutine { cons ->
            userBucketDatabase.child(bucketReference.name)
                .updateChildren(bucketReference.toMutableMap())
                .addListener(cons)
        }

    @Throws(NoUidException::class)
    suspend fun bucketExists(bucketReference: BucketReference): Boolean =
        bucketExists(bucketReference.name)

    @Throws(NoUidException::class)
    suspend fun bucketExists(title: String): Boolean =
        suspendCancellableCoroutine { cons ->

            val callback = ValueListenerBoolean(
                onFinished = { cons.resume(it) },
                onCancel = { cons.cancel(CancellationException()) },
                condition = { iterable -> iterable?.toList()?.isNotEmpty() == true }
            )

            userBucketDatabase
                .child(title)
                .addListenerForSingleValueEvent(callback)
        }

    @Throws(NoUidException::class)
    suspend fun deleteBucket(title: String): Boolean =
        suspendCoroutine { cons ->
            userBucketDatabase
                .child(title)
                .removeValue()
                .addListener(cons)
        }
}


