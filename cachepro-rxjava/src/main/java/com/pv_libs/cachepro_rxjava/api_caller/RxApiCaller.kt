package com.pv_libs.cachepro_rxjava.api_caller

import androidx.lifecycle.LiveData
import com.pv_libs.cachepro_rxjava.ApiResult
import io.reactivex.Observable
import retrofit2.Response

interface RxApiCaller<NetworkResponse> {

    val isApiInProgressLiveData: LiveData<Boolean>

    fun getResponseObservable(callServerOnSubscribe: Boolean = true): Observable<ApiResult<Response<NetworkResponse>>>

    fun fetchFromServer()

}