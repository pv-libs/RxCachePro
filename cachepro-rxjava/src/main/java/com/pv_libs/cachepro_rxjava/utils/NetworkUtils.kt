package com.pv_libs.cachepro_rxjava.utils

import android.content.Context
import android.net.ConnectivityManager

internal class NetworkUtils(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isNetworkConnected: Boolean
        get() {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }

}