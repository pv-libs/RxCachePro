package com.pv_libs.cachepro_rxjava.api_caller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pv_libs.cachepro_rxjava.ApiResult
import com.pv_libs.cachepro_rxjava.utils.isFromCache
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response

internal class RxApiCallerImp<NetworkResponse>(private val builder: Builder<NetworkResponse>) :
    RxApiCaller<NetworkResponse> {

    private val isApiInProgressMutableLiveData = MutableLiveData<Boolean>()

    /**
     * BehaviorSubject to trigger network requests
     */
    private val serverCallsSubject: BehaviorSubject<Observable<Response<NetworkResponse>>> =
        BehaviorSubject.create()

    private var isLoadedAtLeastOnce = false
    private val responseObservable =
        Observable.merge(
            Observable.switchOnNext(serverCallsSubject), builder.getNewCacheCallObservable()
        ).map<ApiResult<Response<NetworkResponse>>> {
            ApiResult.Success(it)
        }.onErrorReturn {
            ApiResult.Error(it as Exception)
        }.filter {
            if (it is ApiResult.Success) {
                // if api call is successful
                if (isLoadedAtLeastOnce && it.data.isFromCache()) {
                    // if data was loaded atLeast once and also if new response is from cache
                    false
                } else {
                    isLoadedAtLeastOnce = true
                    true
                }
            } else
                true
        }


    override val isApiInProgressLiveData: LiveData<Boolean> = isApiInProgressMutableLiveData

    override fun getResponseObservable(callServerOnSubscribe: Boolean): Observable<ApiResult<Response<NetworkResponse>>> {
        if (callServerOnSubscribe) {
            fetchFromServer()
        }
        return responseObservable
    }

    override fun fetchFromServer() {
        serverCallsSubject.onNext(builder.getNewServerCallObservable())
    }

    class Builder<NetworkResponse>(
        private val originalCall: Call<NetworkResponse>,
        private val cacheCallAdapter: CallAdapter<NetworkResponse, Single<Response<NetworkResponse>>>,
        private val serverCallAdapter: CallAdapter<NetworkResponse, Single<Response<NetworkResponse>>>
    ) {
        fun build(): RxApiCaller<NetworkResponse> =
            RxApiCallerImp(this)

        fun getNewCacheCallObservable() =
            cacheCallAdapter.adapt(originalCall.clone()).toObservable()
                .onErrorResumeNext(Observable.empty())!!

        fun getNewServerCallObservable() =
            serverCallAdapter.adapt(originalCall.clone()).toObservable()!!

    }
}