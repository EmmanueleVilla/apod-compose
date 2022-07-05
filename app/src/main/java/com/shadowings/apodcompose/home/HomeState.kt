package com.shadowings.apodcompose.home

import com.google.gson.annotations.SerializedName

data class HomeState(val apods: List<ApodPreview> = listOf())

data class ApodPreview(
    @SerializedName("date")
    val date: String = "",
    @SerializedName("explanation")
    val explanation: String = "",
    @SerializedName("media_type")
    val mediaType: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String = ""
)