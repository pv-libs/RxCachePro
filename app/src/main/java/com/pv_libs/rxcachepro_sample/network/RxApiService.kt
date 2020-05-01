package com.pv_libs.rxcachepro_sample.network

import com.pv_libs.cachepro_rxjava.ApiResult
import com.pv_libs.cachepro_rxjava.RxApiCaller
import com.pv_libs.rxcachepro_sample.models.GetUsersResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface RxApiService {


    @GET("/api/users")
    fun getUsersObservable(): Observable<ApiResult<Response<GetUsersResponse>>>

    @GET("/api/users")
    fun getUsersApiCaller(): RxApiCaller<GetUsersResponse>

}