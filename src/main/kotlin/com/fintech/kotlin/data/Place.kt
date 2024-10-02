package com.fintech.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Place(val id: Long,
            val title: String?,
            val slug: String?,
            val address: String?,
            @SerialName("site_url")
            val siteUrl: String?,
            @SerialName("is_closed")
            val isClosed: Boolean?,
            val location: String?) {
}