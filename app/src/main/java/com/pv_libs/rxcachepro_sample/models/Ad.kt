package com.pv_libs.rxcachepro_sample.models


import com.google.gson.annotations.SerializedName

data class Ad(
    @SerializedName("company")
    val company: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("text")
    val text: String
)