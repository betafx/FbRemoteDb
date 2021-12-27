# FbRemoteDb

[![](https://jitpack.io/v/betafx/FbRemoteDb.svg)](https://jitpack.io/#betafx/FbRemoteDb)

Simple access to Firebase Realtime Database.

Everything is inside a Bucket, there you can store your items. This is for simplifying the access to
your data.

You can create a Bucket with a name, a password and a list of BucketItem. You can join a Bucket with
a name, a password.

[1. How to use in code](#howtouseincode)

[2. How to setup project](#howtosetupfirebase)

[3. Limitations](#limitationsofmodule)

[4. Troubleshooting](#troubleshootingofmodule)

[5. Last words](#lastwords)

[6. License](#licenseofmodule)

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
    private val fbRepo by lazy {
        FbRemoteDb(MyData::class.java).apply {
            version = "1" // Optional
        }
    }

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
    plugins {
        id 'com.google.gms.google-services' version '4.3.10'
    }
}
dependencyResolutionManagement {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

2. Add following to ``app/build.gradle``:

```gradle
plugins {
    id 'com.google.gms.google-services'
}
dependencies {
    implementation 'com.github.betafx:FbRemoteDb:0.8.1-alpha'
}
```

3. Register your app on https://console.firebase.google.com. Download google-services.json into
   project.

4. Create a new Realtime Database on https://console.firebase.google.com. Change the rules to this:

```json
{
  "rules": {
    ".read": "false",
    ".write": "false",
    "user": {
      ".read": "false",
      ".write": "false",
      "$user_id": {
        ".write": "$user_id === auth.uid",
        ".read": "$user_id === auth.uid"
      }
    },
    "buckets": {
      ".read": "false",
      ".write": "false",
      "$bucket": {
        ".read": "false",
        ".write": "false",
        "$version": {
          ".read": "false",
          ".write": "false",
          "$pw": {
            ".read": "auth.uid != null",
            ".write": "auth.uid != null"
          }
        }
      }
    }
  }
}
```

<a name="limitationsofmodule"></a>

## Limitations

* Do not use this for sensitive data, seriously.
* No login mechanism, the right user has to be logged in or nothing will work.
* No server-side validation of data.
* No encryption of the data stored on the Firebase-Server. Data in database is visible for you.
  Implement your custom encryption and store the key on the device.
* Do not edit the database online on https://console.firebase.google.com, I don't know what will
  happen. Maybe your app will crash.

<a name="troubleshootingofmodule"></a>

## Troubleshooting

```
java.lang.IllegalStateException: Default FirebaseApp is not initialized in this process com.app.myapplication. Make sure to call FirebaseApp.initializeApp(Context) first.
```

You forgot to add `id 'com.google.gms.google-services'` in ``app/build.gradle``.

```
Execution failed for task ':app:processDebugGoogleServices'.
> File google-services.json is missing. The Google Services Plugin cannot function without it.
```

See step 3 in "How to setup project"

<a name="lastwords"></a>

## Last words

I was annoyed to move the same code over again, when using it in my own projects, so I decided to
move it here. I am happy if you contribute, fork, clone or whatever to this repo, also opening
issues is really appreciated. Feel free to contact my and happy coding :)

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
