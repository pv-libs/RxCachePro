# RxCachePro

RxCachePro is a Kotlin library, which provide features to take full advantage of HttpCache.

If you are already using RxJava2 for network calls, you can optimize your API calls and also enable offline experience with very few changes.

### How to include in your project

###### Add the dependency to your `build.gradle`:

```groovy
implementation 'com.pv-libs.CachePro:RxCachePro:0.1.0'
```

Setup
---
#### 1. Initializing ``OkHttpClient``
 - Create an instance of ``CachePro``
 - attach the cachePro instance to ``OkHttpClient.Builder`` as shown
```kotlin
val cachePro = CachePro.Builder(context)
    .setEnableOffline(true)    // default true
    .setForceCache(true)       // default true
    .build()

val okHttpClient = OkHttpClient.Builder()
    .cache(cache)               // need to add cache to work as expected
    .attachCachePro(cachePro)   // attaching cachePro to OkHttpClient
    .build()
```

#### 2. Initializing ``Retorfit``
 - Set the above created ``okHttpClient`` as client.
 - ``RxCacheProCallAdapter.Factory`` should be the first CallAdapterFactory added to retrofit
 - then you also need to add another CallAdapterFactory which supports RxJava2
```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://reqres.in")
    .client(okHttpClient)
    .addCallAdapterFactory(RxCacheProCallAdapter.Factory())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

## Features

#### 1) Observable from retrofit 

Modify return type of your API call to ``Observable<Response<NetworkResponse>>``

which ``onSubscribe`` provides the cache response immediately and in background validates the cache response, and notifies if the current cache response is invalid.  
```kotlin
@GET("/api/users")
fun getUsersListObservable(): Observable<Response<GetUsersResponse>>
```

#### 2) RxApiCaller
Modify return type of your API call to ``RxApiCaller<NetworkResponse>``

**RxApiCaller** provides an ``Observable`` and a function ``fetchFromServer()`` which enables you to implement features like swipe to refresh much more efficiently.

By default RxApiCaller functions exactly like the above observable but it allows for multiple triggers of network calls, which happen in background and you only get notified if there is any change
  
```kotlin
@GET("/api/users")
fun getUsersListObservable(): RxApiCaller<GetUsersResponse>
```

```kotlin
class UsersViewModel : ViewModel{
    private val usersListApiCaller: RxApiCaller<GetUsersResponse> = dataManager.getUsersApiCaller()

    init{
        // should only be called once
        usersListApiCaller.getResponseObservable()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                // triggered multiple times, if and only if the Data id modified
                when (it) {
                    is ApiResult.Success -> {
                        onResponse(it.data)
                    }
                    is ApiResult.Error -> {
                        showToast(it.exception.localizedMessage)
                    }
                }
            }
    }

    fun refreshData(){
        // triggers a new network request and notifies through above Observable if there is any change in data
        usersListApiCaller.fetchFromServer()
    }

    // RxApiCaller also provides a LiveData which informs if there is any network request currently running in background.
    val inApiRunningLiveData = usersListApiCaller.isApiInProgressLiveData

}
```
Checkout the given sample 'RxSampleActivity'

#### 2) Instant Offline Support
If application makes a ``GET`` request when device is not connected to any network, RxCachePro will make retrofit check if there is any data in ``CACHE`` and return it.  


#### 3) Ability to Force Cache
RxCachePro provides ability to force every ``GET`` request to use **Cache**, if your api responses doesn't contain **Cache-Control**.

This applies to every ``GET`` API request, to disable cache for specific API use ``@ApiNoCache`` 
```kotlin
@ApiNoCache
@GET("/api/users")
fun getUsersListSingle(): Single<Response<GetUsersResponse>>
```




