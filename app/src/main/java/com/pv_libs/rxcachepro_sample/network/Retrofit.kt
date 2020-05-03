package com.pv_libs.rxcachepro_sample.network

import android.content.Context
import com.pv_libs.cachepro_rxjava.CachePro
import com.pv_libs.cachepro_rxjava.adapters.RxCacheProCallAdapter
import com.pv_libs.cachepro_rxjava.attachCachePro
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object Retrofit {

    private const val CACHE_SIZE = 100 * 1024 * 1020 // 50MB

    fun getRxApiService(context: Context): RxApiService {
        return provideRetrofit(context.applicationContext)
            .create(RxApiService::class.java)
    }

    private fun provideRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://reqres.in")
            .client(provideOkHttpClient(context))
            .addCallAdapterFactory(RxCacheProCallAdapter.Factory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideOkHttpClient(context: Context): OkHttpClient {

        val cache = provideCache(context)

        val okHttpClientBuilder = OkHttpClient.Builder()

        okHttpClientBuilder.cache(cache)

        val cachePro = CachePro.Builder(context).build()
        okHttpClientBuilder.attachCachePro(cachePro)

        // For observing api calls with GUI
        okHttpClientBuilder.addInterceptor(ChuckInterceptor(context))
        okHttpClientBuilder.addNetworkInterceptor(ChuckInterceptor(context))


        return okHttpClientBuilder.build()
    }

    private fun provideCache(context: Context): Cache {
        val cacheFolder = File(context.cacheDir, "apiCache")
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs()
        }
        return Cache(cacheFolder, CACHE_SIZE.toLong())
    }

}