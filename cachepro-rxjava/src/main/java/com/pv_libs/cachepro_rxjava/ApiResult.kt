package com.pv_libs.cachepro_rxjava

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    class Error(val exception: Exception) : ApiResult<Nothing>()
}
