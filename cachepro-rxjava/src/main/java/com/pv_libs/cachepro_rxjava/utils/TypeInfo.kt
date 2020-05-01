package com.pv_libs.cachepro_rxjava.utils

import com.pv_libs.cachepro_rxjava.ApiResult
import com.pv_libs.cachepro_rxjava.RxApiCaller
import io.reactivex.Observable
import retrofit2.Response
import java.lang.reflect.Type


class ReturnTypeInfo(
    val returnType: Type,
    val responseType: Type,
    val outerLayer: Type,
    val hasApiResult: Boolean,
    val hasResponse: Boolean
) {
    fun validate(): Boolean {
        // Validating for RxApiCaller
        if (outerLayer == RxApiCaller::class.java && !hasApiResult && !hasResponse) {
            // returnType -> RxApiCaller<NetworkResponse>  -- supported
            // RxApiCaller should not have ApiResult or Response as childType
            return true
        }
        // -----

        // Validating for Observable
        if (outerLayer == Observable::class.java) {

            // Validating for ApiResult under Observable
            if (hasApiResult && hasResponse) {
                // returnType -> Observable<ApiResult<Response<NetworkResponse>>> -- supported
                return true
            }
            if (hasApiResult && !hasResponse) {
                // returnType -> Observable<ApiResult<NetworkResponse>> -- not supported
                return false
            }
            // ------

            // Validating for Response under Observable
            // returnType -> Observable<Response<NetworkResponse>> -- supported
            // returnType -> Observable<NetworkResponse> -- not supported
            return hasResponse
        }

        return false
    }
}


internal fun Type.getReturnTypeInfo(): ReturnTypeInfo? {
    val outerLayer = rawType
    if (outerLayer != Observable::class.java && rawType != RxApiCaller::class.java) {
        // Right now we only support Observable and RxApiCaller
        return null
    }

    var hasApiResult = false
    var hasResponse = false

    var subType = childType ?: return null

    // checking if return type contains ApiResult
    if (subType.rawType == ApiResult::class.java) {
        hasApiResult = true
        // setting the child type of ApiResult as the new subType
        subType = subType.childType ?: return null
    }

    // checking if return type contains Response
    if (subType.rawType == Response::class.java) {
        hasResponse = true
        // setting the child type of Response as the new subType
        subType = subType.childType ?: return null
    }
    val responseType = subType

    return ReturnTypeInfo(
        this,
        responseType,
        outerLayer,
        hasApiResult,
        hasResponse
    )
}
