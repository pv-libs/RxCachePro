package com.pv_libs.cachepro_rxjava.interceptors

import android.content.Context
import com.pv_libs.cachepro_rxjava.annotations.ApiCache
import com.pv_libs.cachepro_rxjava.utils.CACHE_CONTROL
import com.pv_libs.cachepro_rxjava.utils.NetworkUtils
import com.pv_libs.cachepro_rxjava.utils.NoConnectionError
import okhttp3.Interceptor
import okhttp3.Response

class CacheProInterceptor(context: Context) : Interceptor {

    private val networkUtils = NetworkUtils(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()


        val isAnnotatedAsApiCacheObservable =
            false // todo request.hasAnnotation(ApiCache::class.java)
        val hasInternet = networkUtils.isNetworkConnected
        val forceCacheCall = request.tag() is ApiCache


        if (!forceCacheCall && isAnnotatedAsApiCacheObservable && !hasInternet) {
            throw NoConnectionError()
        }
        //Trying to request cache data when network is not connected
        if (forceCacheCall || !hasInternet) {
            // getting data from cache when there no internet

            request = request.newBuilder()
                .addHeader(CACHE_CONTROL, "max-stale ,private, only-if-cached").build()
        }

        return chain.proceed(request)
    }
}