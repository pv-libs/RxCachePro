package com.pv_libs.rxcachepro_sample.network

import com.pv_libs.cachepro_rxjava.ApiResult
import com.pv_libs.cachepro_rxjava.annotations.ApiNoCache
import com.pv_libs.cachepro_rxjava.api_caller.RxApiCaller
import com.pv_libs.rxcachepro_sample.models.GetUsersResponse
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET

interface RxApiService {

    @ApiNoCache                                                          // use @ApiNoCache to disable the cache
    @GET("/api/users")
    fun getUsersSingle(): Single<ApiResult<Response<GetUsersResponse>>>  //  sample api for disabling the cache

    @GET("/api/users")
    fun getUsersObservable(): Observable<Response<GetUsersResponse>>     // sample api with Observable

    @GET("/api/users")
    fun getUsersApiCaller(): RxApiCaller<GetUsersResponse>               // sample api with RxApiCaller

}