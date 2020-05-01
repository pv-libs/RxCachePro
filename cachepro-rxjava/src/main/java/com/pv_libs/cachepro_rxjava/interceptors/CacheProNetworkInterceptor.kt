package com.pv_libs.cachepro_rxjava.interceptors

import com.pv_libs.cachepro_rxjava.BuildConfig
import com.pv_libs.cachepro_rxjava.utils.CACHE_CONTROL
import com.pv_libs.cachepro_rxjava.utils.GET
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Response


/**
 * This [Interceptor] modifies network [Response]. To force [Cache] to save the [Response],
 * if API method is GET and is not annotated with [ApiNoCache]
 */
class CacheProNetworkInterceptor : Interceptor {

    /**
     * intercepting the [Response] and modifies network response to force [Cache] to save the [Response],
     * if API method is GET and is not annotated with [ApiNoCache]
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val isAnnotatedAsNoCache = false // todo request.hasAnnotation(ApiNoCache::class.java)
        val isGetRequest = request.method().equals(GET, true)


        val requestBuilder = request.newBuilder()
        if (!isAnnotatedAsNoCache && isGetRequest) {
            if (BuildConfig.DEBUG) {
                // overriding the cache control send by the server
                requestBuilder.removeHeader(CACHE_CONTROL)
            }
            response.newBuilder().addHeader(CACHE_CONTROL, "private, max-age=0")
        }

        return response
    }


}