package de.betafx.fbremotedb.remote_db

import android.app.Application
import android.os.Build
import de.betafx.fbremotedb.BucketItem
import de.betafx.fbremotedb.remote_db.models.Bucket
import de.betafx.fbremotedb.remote_db.models.BucketInternal
import de.betafx.fbremotedb.remote_db.models.BucketReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import java.security.MessageDigest


internal class FbRemoteDbImpl<T : BucketItem>(private val clazz: Class<T>, version: String) {

    private val remoteBuckets = RemoteBuckets<T>(version)

    private val userBuckets = RemoteUserBuckets(version)

    suspend fun deleteBucketForUser(name: String): Boolean {
        // check for existing in user repo
        if (!userBuckets.bucketExists(name)) return true
        return userBuckets.deleteBucket(name)
    }

    suspend fun fetchBucketsList(): List<Bucket<T>> {
        val userBucketsList = userBuckets.getPrivateBucketReferences()
        val list = mutableListOf<BucketInternal<T>>()
        userBucketsList.forEach { bucketReference ->
            remoteBuckets.getBucketWithRef(bucketReference, clazz)?.let {
                list.add(it)
            }
        }
        return list.map { bucketInternal ->
            Bucket(content = bucketInternal.content)
                .apply { name = bucketInternal.name }
        }
    }

    suspend fun fetchBucket(name: String): Bucket<T>? {
        val userBucketsList = userBuckets.getPrivateBucketReferences()
        val bucketReference = userBucketsList.firstOrNull { it.name == name } ?: return null
        val bucketInternal = remoteBuckets.getBucketWithRef(bucketReference, clazz) ?: return null
        return Bucket(content = bucketInternal.content).apply { this.name = bucketInternal.name }

    }

    suspend fun updateBucket(name: String, bucket: Bucket<T>): Boolean {
        val userBucketsList = userBuckets.getPrivateBucketReferences()
        val list = mutableListOf<Pair<BucketReference, BucketInternal<T>>>()
        userBucketsList.forEach { bucketReference ->
            if (remoteBuckets.bucketExists(bucketReference)) {
                remoteBuckets.getBucketWithRef(bucketReference, clazz)?.let {
                    list.add(Pair(bucketReference, it))
                }
            }
        }
        val remoteBucketPair = list.firstOrNull { it.first.name == name } ?: return false
        return remoteBuckets.createBucketWithRef(
            remoteBucketPair.first,
            BucketInternal(name, bucket.content)
        )
    }

    suspend fun createBucket(
        name: String,
        password: String,
        bucket: Bucket<T>
    ): Boolean {
        val bucketReference = BucketReference(name, password.hashed())
        // check for existing remote
        if (remoteBuckets.bucketExists(bucketReference)) return false
        // check for existing in user repo
        if (userBuckets.bucketExists(bucketReference)) return false
        // create in user db
        if (!userBuckets.storePrivateBucketReference(bucketReference)) return false
        // create public bucket
        return remoteBuckets.createBucketWithRef(
            bucketReference, BucketInternal(name, bucket.content)
        )
    }

    suspend fun joinPublicBucket(name: String, password: String): Boolean {
        val bucketReference = BucketReference(name, password.hashed())
        // check for existing remote
        if (!remoteBuckets.bucketExists(bucketReference)) return false
        // check for existing in user repo
        if (userBuckets.bucketExists(bucketReference)) return false
        return userBuckets.storePrivateBucketReference(bucketReference)
    }

    suspend fun getBucketAsFlow(name: String): Flow<Bucket<T>>? {
        val userBucketsList = userBuckets.getPrivateBucketReferences()
        val bucketReference = userBucketsList.firstOrNull { it.name == name } ?: return null
        return remoteBuckets.getBucketAsFlow(bucketReference, clazz)
    }

    suspend fun getTAsFlow(name: String, id: String): Flow<T>? {
        val userBucketsList = userBuckets.getPrivateBucketReferences()
        val bucketReference = userBucketsList.firstOrNull { it.name == name } ?: return null
        return remoteBuckets.getTAsFlow(bucketReference, clazz)
            ?.filterNotNull()
            ?.filter { it.id == id }
    }


    private fun String.hashed(): String {
        val salt = "S,Cj.7Tqt7vW.b<!#s2wd;kf,;2_"
        val pepper =
            "/&'TkV}*}r.P6f6}n7t8WC3`wuhw!b" +
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        Application.getProcessName()
                    } else {
                        ""
                    }
        val bytes = (salt + this + pepper).toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) } + clazz.canonicalName?.filter { it.isLetter() }
    }

}
