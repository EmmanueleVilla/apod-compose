package com.shadowings.apodcompose.home

import com.google.gson.annotations.SerializedName

data class HomeState(val apods: List<ApodModel> = listOf())

data class ApodModel(
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
    @SerializedName("hdurl")
    val hdUrl: String = "",
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String = ""
) {
    fun thumbnail() = if (mediaType == "image") url else thumbnailUrl
}