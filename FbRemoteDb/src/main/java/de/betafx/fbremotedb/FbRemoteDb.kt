package de.betafx.fbremotedb

import de.betafx.fbremotedb.remote_db.FbRemoteDbImpl
import de.betafx.fbremotedb.remote_db.models.Bucket
import kotlinx.coroutines.flow.Flow

class FbRemoteDb<T : BucketItem>(clazz: Class<T>, name: String) {

    var version: String = name

    private val repo = FbRemoteDbImpl(clazz, version)

    @Throws(NoUidException::class)
    suspend fun deleteBucketForUser(name: String): Boolean =
        repo.deleteBucketForUser(name)

    @Throws(NoUidException::class)
    suspend fun fetchBucketsList(): List<Bucket<T>> =
        repo.fetchBucketsList()

    @Throws(NoUidException::class)
    suspend fun fetchBucket(name: String): Bucket<T>? =
        repo.fetchBucket(name)

    @Throws(NoUidException::class)
    suspend fun updateBucket(bucket: Bucket<T>): Boolean =
        repo.updateBucket(bucket.name, bucket)

    @Throws(NoUidException::class)
    suspend fun createBucket(name: String, password: String, bucketList: List<T>): Boolean =
        repo.createBucket(name, password, Bucket(bucketList))

    @Throws(NoUidException::class)
    suspend fun joinPublicBucket(name: String, password: String): Boolean =
        repo.joinPublicBucket(name, password)

    @Throws(NoUidException::class)
    suspend fun getBucketAsFlow(name: String): Flow<Bucket<T>>? =
        repo.getBucketAsFlow(name)

    @Throws(NoUidException::class)
    suspend fun getTAsFlow(name: String, id: String): Flow<T>? =
        repo.getTAsFlow(name, id)

}
