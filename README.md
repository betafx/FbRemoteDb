# FbRemoteDb

Simple access to Firebase Realtime Database.

Everything is inside a Bucket, there you can store your items. This is for simplifying the access to
your data..

You can create a Bucket with a name, a password and a list of BucketItem. You can join a Bucket with
a name, a password.

[1. How to use in code](#howtouseincode)

[2. How to setup project](#howtosetupfirebase)

[3. Limitations](#limitationsofmodule)

[4. Last words](#lastwords)

[5. License](#licenseofmodule)

<a name="howtouseincode"></a>

## How to use in code

Define a class for the content of the Buckets, which extends BucketItem and provides an empty
constructor.

```kotlin
data class MyData(override val id: String, val secretCodes: List<String>) : BucketItem {
    constructor() : this("", emptyList())
}
```

Now you can get the repository and use it, e.g. in a ViewModel.

```kotlin
class AViewModel : ViewModel() {
    private val fbRepo by lazy { FBRepo(MyData::class.java) }

    fun createBuckets() {
        viewModelScope.launch {
            // TODO: Here do, what you need:  fbRepo. 
        }

    }
}
```

The signatures are like this. Everything throws a NoUidException if there is no with FirebaseAuth
logged in user. Additionally, it is your responsibility to ensure that the correct user is logged
in.

```kotlin
@Throws(NoUidException::class)
suspend fun deleteBucketForUser(name: String): Boolean

@Throws(NoUidException::class)
suspend fun fetchBucketsList(): List<Bucket<T>>

@Throws(NoUidException::class)
suspend fun fetchBucket(name: String): Bucket<T>?

@Throws(NoUidException::class)
suspend fun updateBucket(bucket: Bucket<T>): Boolean

@Throws(NoUidException::class)
suspend fun createBucket(name: String, password: String, bucketList: List<T>): Boolean

@Throws(NoUidException::class)
suspend fun joinPublicBucket(name: String, password: String): Boolean

@Throws(NoUidException::class)
suspend fun getBucketAsFlow(name: String): Flow<Bucket<T>>?

@Throws(NoUidException::class)
suspend fun getTAsFlow(name: String, id: String): Flow<T>?
```

<a name="howtosetupfirebase"></a>

## How to setup project

1. Add following to ``settings.gradle``:

```gradle
pluginManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
    plugins {
        id 'com.google.gms.google-services' version '4.3.10'
    }
}
```

2. Add following to ``app/build.gradle``:
3. x
4. x

<a name="limitationsofmodule"></a>

## Limitations

* No login, the right user has to be logged in or nothing will work.
* No server-side validation of data
* No encryption of the data stored on the Firebase-Server, which is visible for you. Implement your
  custom encryption and store the key on the device.
* Do not edit the database online on https://console.firebase.google.com, I don't know what will
  happen. Maybe your app will crash.

<a name="lastwords"></a>

## Last words

<a name="licenseofmodule"></a>

## License

<pre>
Copyright 2021 betafx

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
</pre>
