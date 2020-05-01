package com.pv_libs.cachepro_rxjava.annotations


@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
internal annotation class ApiCache

@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
internal annotation class ApiNoCache


internal class Annotations {
    private val apiNoCacheAnnotation: ApiNoCache by lazy {
        Annotations::class.java.getDeclaredMethod("getApiNoCache")
            .getAnnotation(ApiNoCache::class.java)
    }

    private val apiCacheAnnotation: ApiCache by lazy {
        Annotations::class.java.getDeclaredMethod("getApiCache").getAnnotation(ApiCache::class.java)
    }

    @ApiNoCache
    fun getApiNoCache() = apiNoCacheAnnotation

    @ApiCache
    fun getApiCache() = apiNoCacheAnnotation
}




