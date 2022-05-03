package com.discovery.codingassesment.data.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class NewsHeadlinesData(
    @SerializedName("status") val status : String,
    @SerializedName("totalResults") val totalResults : Int,
    @SerializedName("articles") val articles : List<Articles>,


) {
    data class Articles(
        @SerializedName("source") val source : NewsSource,
        @SerializedName("author") val author : String,
        @SerializedName("title") val title : String,
        @SerializedName("description") val description : String,
        @SerializedName("url") val newsURL : String,
        @SerializedName("urlToImage") val urlToImage : String,
        @SerializedName("publishedAt") val publishedAt : String,
        @SerializedName("content") val newsContent : String,
    )

    data class NewsSource(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
    )
}
