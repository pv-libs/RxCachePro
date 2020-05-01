package com.pv_libs.cachepro_rxjava.adapters

import com.pv_libs.cachepro_rxjava.ApiResult
import com.pv_libs.cachepro_rxjava.RxApiCaller
import com.pv_libs.cachepro_rxjava.RxApiCallerImp
import com.pv_libs.cachepro_rxjava.annotations.Annotations
import com.pv_libs.cachepro_rxjava.annotations.ApiNoCache
import com.pv_libs.cachepro_rxjava.utils.ReturnTypeInfo
import com.pv_libs.cachepro_rxjava.utils.add
import com.pv_libs.cachepro_rxjava.utils.getReturnTypeInfo
import com.pv_libs.cachepro_rxjava.utils.wrapWith
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type

class RxApiCallerAdapter<NetworkResponse>(
    private val returnTypeInfo: ReturnTypeInfo,
    private val cacheCallAdapter: CallAdapter<NetworkResponse, Single<Response<NetworkResponse>>>,
    private val serverCallAdapter: CallAdapter<NetworkResponse, Single<Response<NetworkResponse>>>
) : CallAdapter<NetworkResponse, Any> {

    override fun adapt(call: Call<NetworkResponse>): Any {
        val apiCaller = RxApiCallerImp.Builder(
            call,
            cacheCallAdapter,
            serverCallAdapter
        ).build()

        if (returnTypeInfo.outerLayer == RxApiCaller::class.java) {
            // returnType is RxApiCaller<NetworkResponse>
            return apiCaller
        }
        if (returnTypeInfo.outerLayer == Observable::class.java) {

            if (returnTypeInfo.hasApiResult && returnTypeInfo.hasResponse) {
                // returnType is Observable<ApiResult<Response<NetworkResponse>>>
                return apiCaller.getResponseObservable()
            }

            if (returnTypeInfo.hasResponse) {
                // returnType is Observable<Response<NetworkResponse>>
                return apiCaller.getResponseObservable()
                    .map {
                        when (it) {
                            is ApiResult.Success -> it.data
                            is ApiResult.Error -> throw it.exception
                        }
                    }
            }
        }
        throw IllegalAccessException("unSupported returnType - ${returnTypeInfo.returnType}")
    }

    override fun responseType(): Type {
        return returnTypeInfo.responseType
    }

    class Factory : CallAdapter.Factory() {

        override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, *>? {

            if (annotations.any { it is ApiNoCache }) {
                return null
            }
            val returnTypeInfo = returnType.getReturnTypeInfo() ?: return null

            if (!returnTypeInfo.validate()) {
                return null
            }

            val singleResponseType = returnTypeInfo.responseType.wrapWith(Response::class.java)
                .wrapWith(Single::class.java)

            val cacheCallAdapter = retrofit.nextCallAdapter(
                this,
                singleResponseType,
                annotations.add(ANNOTATIONS.getApiNoCache())
            ) as CallAdapter<Any?, Single<Response<Any?>>>
            val serverCallAdapter = retrofit.nextCallAdapter(
                this,
                singleResponseType,
                annotations.add(ANNOTATIONS.getApiCache())
            ) as CallAdapter<Any?, Single<Response<Any?>>>

            return RxApiCallerAdapter(
                returnTypeInfo,
                cacheCallAdapter, serverCallAdapter
            )
        }
    }

    companion object {
        private val ANNOTATIONS = Annotations()
    }
}