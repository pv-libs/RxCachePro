package com.pv_libs.cachepro_rxjava

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response

internal class RxApiCallerImp<NetworkResponse>(private val builder: Builder<NetworkResponse>) :
    RxApiCaller<NetworkResponse> {

    private var callServerOnSubscribe = true

    private val isApiInProgressMutableLiveData = MutableLiveData<Boolean>()

    private val behaviourSubject: BehaviorSubject<Observable<Response<NetworkResponse>>> =
        BehaviorSubject.createDefault(builder.getNewCacheCallObservable())

    private val apiResultObservable =
        behaviourSubject.map {
            it.map<ApiResult<Response<NetworkResponse>>> { response ->
                ApiResult.Success(response)
            }.onErrorReturn { throwable ->
                ApiResult.Error(throwable as Exception)
            }
        }

    private val responseObservable = Observable.switchOnNext(apiResultObservable)


    override val isApiInProgressLiveData: LiveData<Boolean> = isApiInProgressMutableLiveData

    override fun getResponseObservable(callServerOnSubscribe: Boolean): Observable<ApiResult<Response<NetworkResponse>>> {
        this.callServerOnSubscribe = callServerOnSubscribe
        return responseObservable
    }

    override fun fetchFromServer() {
        behaviourSubject.onNext(builder.getNewServerCallObservable())
    }

    class Builder<NetworkResponse>(
        private val originalCall: Call<NetworkResponse>,
        private val cacheCallAdapter: CallAdapter<NetworkResponse, Single<Response<NetworkResponse>>>,
        private val serverCallAdapter: CallAdapter<NetworkResponse, Single<Response<NetworkResponse>>>
    ) {
        fun build(): RxApiCaller<NetworkResponse> = RxApiCallerImp(this)

        fun getNewCacheCallObservable() =
            cacheCallAdapter.adapt(originalCall.clone()).toObservable()

        fun getNewServerCallObservable() =
            serverCallAdapter.adapt(originalCall.clone()).toObservable()

    }
}