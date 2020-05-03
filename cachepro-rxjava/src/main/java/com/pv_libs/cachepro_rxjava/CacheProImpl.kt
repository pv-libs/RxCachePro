package com.pv_libs.cachepro_rxjava

import com.pv_libs.cachepro_rxjava.interceptors.CacheProInterceptor
import com.pv_libs.cachepro_rxjava.interceptors.CacheProNetworkInterceptor
import com.pv_libs.cachepro_rxjava.utils.NetworkUtils
import okhttp3.Interceptor

internal class CacheProImpl internal constructor(private val builder: CachePro.Builder) : CachePro {

    override fun getNetworkInterceptor(): Interceptor {
        return CacheProNetworkInterceptor(builder.forceCache)
    }

    override fun getInterceptor(): Interceptor {
        return CacheProInterceptor(getNetworkUtils(), builder.enableOffline)
    }

    private fun getNetworkUtils() = NetworkUtils(builder.context)

}