package de.betafx.fbremotedb.remote_db

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.betafx.fbremotedb.remote_db.models.Bucket
import de.betafx.fbremotedb.remote_db.models.BucketInternal
import de.betafx.fbremotedb.remote_db.models.BucketReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class RemoteBuckets<T> {

    private val database = Firebase.database

    private val bucketDatabase = database.getReference("buckets")


    suspend fun bucketExists(bucketReference: BucketReference): Boolean =
        suspendCancellableCoroutine { cons ->
            val callback = ValueListenerBoolean(
                onFinished = { cons.resume(it) },
                onCancel = { cons.cancel(CancellationException()) },
                condition = { iterable -> iterable?.toList()?.isNotEmpty() == true }
            )

            bucketDatabase
                .child(bucketReference.name)
                .child(bucketReference.password)
                .addListenerForSingleValueEvent(callback)
        }

    suspend fun createBucketWithRef(
        bucketReference: BucketReference,
        bucketInternal: BucketInternal<*>
    ): Boolean =
        suspendCoroutine { cons ->
            bucketDatabase
                .child(bucketReference.name)
                .child(bucketReference.password)
                .updateChildren(bucketInternal.toMutableMap())
                .addListener(cons)
        }


    suspend fun getBucketWithRef(
        bucketReference: BucketReference,
        clazz: Class<T>
    ): BucketInternal<T>? =
        suspendCancellableCoroutine { cons ->

            val callback = ValueListenerList(
                onFinished = { list: List<T> ->
                    cons.resume(
                        BucketInternal(bucketReference.name, list)
                    )
                },
                onCancel = { cons.cancel(CancellationException()) },
                clazz = clazz
            )

            bucketDatabase
                .child(bucketReference.name)
                .child(bucketReference.password)
                .child("content")
                .addListenerForSingleValueEvent(callback)
        }

    suspend fun getBucketAsFlow(
        bucketReference: BucketReference,
        clazz: Class<T>
    ): Flow<Bucket<T>>? {
        if (!bucketExists(bucketReference)) return null

        return callbackFlow {

            val callback = ChildEventListenerList(
                onChildAdded = {
                    trySendBlocking(it.toBucket(bucketReference.name))
                },
                onChildChanged = {
                    trySendBlocking(it.toBucket(bucketReference.name))
                },
                onChildRemoved = {
                    trySendBlocking(it.toBucket(bucketReference.name))
                },
                onChildMoved = {
                    trySendBlocking(it.toBucket(bucketReference.name))
                },
                onCancelled = {
                    // nope
                 },
                clazz = clazz
            )

            val bucketChild = bucketDatabase
                .child(bucketReference.name)
                .child(bucketReference.password)

            bucketChild.addChildEventListener(callback)

            awaitClose { bucketChild.removeEventListener(callback) }
        }
    }

    suspend fun getTAsFlow(
        bucketReference: BucketReference,
        clazz: Class<T>
    ): Flow<T>? {
        if (!bucketExists(bucketReference)) return null

        return callbackFlow {

            val callback = ChildEventListenerObject(
                onChildAdded = {
                    trySendBlocking(it)
                },
                onChildChanged = {
                    trySendBlocking(it)
                },
                onChildRemoved = {
                    trySendBlocking(it)
                },
                onChildMoved = {
                    trySendBlocking(it)
                },
                onCancelled = {
                    // nope
                },
                clazz = clazz
            )

            val bucketChild = bucketDatabase
                .child(bucketReference.name)
                .child(bucketReference.password)
                .child("content")

            bucketChild.addChildEventListener(callback)

            awaitClose { bucketChild.removeEventListener(callback) }
        }
    }

    private fun List<T>.toBucket(title_: String): Bucket<T> = Bucket(
        this
    ).apply { name = title_ }

}
