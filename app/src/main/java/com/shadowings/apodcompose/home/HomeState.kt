package com.shadowings.apodcompose.home

import com.google.gson.annotations.SerializedName

/**
 * Contains the list of apods of the homepage
 */
data class HomeState(val apods: List<ApodModel> = listOf())

/**
 * APOD model data class
 */
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
    /**
     * Returns [url] if the apod is an image, [thumbnailUrl] is it's a video
     */
    fun toThumbnail() = if (mediaType == "image") url else thumbnailUrl

    /**
     * Returns true if the apod is a video
     */
    fun isVideo() = mediaType == "video"
}