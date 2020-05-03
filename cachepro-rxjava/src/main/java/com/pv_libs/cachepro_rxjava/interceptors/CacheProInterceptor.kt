package com.pv_libs.cachepro_rxjava.interceptors

import com.pv_libs.cachepro_rxjava.annotations.ApiCache
import com.pv_libs.cachepro_rxjava.annotations.ApiNoCache
import com.pv_libs.cachepro_rxjava.annotations.ForceCacheCall
import com.pv_libs.cachepro_rxjava.annotations.ForceNetworkCall
import com.pv_libs.cachepro_rxjava.utils.*
import okhttp3.Interceptor
import okhttp3.Response

internal class CacheProInterceptor(
    private val networkUtils: NetworkUtils,
    private val enableOffline: Boolean
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()


        val isGetRequest = request.method().equals(GET, true)
        val hasInternet = networkUtils.isNetworkConnected

        val isAnnotatedAsApiNoCache = request.hasAnnotation(ApiNoCache::class.java)
        val isAnnotatedAsApiCache = request.hasAnnotation(ApiCache::class.java)
        val forceCacheCall = request.hasAnnotation(ForceCacheCall::class.java)
        val forceNetworkCall = request.hasAnnotation(ForceNetworkCall::class.java)


        // can't do network call if there is no internet
        if (!hasInternet && (!isGetRequest || forceNetworkCall || isAnnotatedAsApiNoCache)) {
            throw NoConnectionError()
        }

        //Trying to request cache data when network is not connected
        if (forceCacheCall || (!hasInternet && enableOffline) || (isAnnotatedAsApiCache && !hasInternet)) {
            // getting data from cache when there no internet

            request = request.newBuilder()
                .addHeader(CACHE_CONTROL, "max-stale ,private, only-if-cached").build()
        }

        return chain.proceed(request)
    }

}