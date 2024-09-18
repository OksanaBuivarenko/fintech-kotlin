package com.fintech.kotlin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.*
import kotlin.math.exp

@Serializable
data class News(val id: Long,
                val title: String,
                val place: Place?,
                val description: String,
                @SerialName("site_url")
                val siteUrl: String,
                @SerialName("favorites_count")
                val favoritesCount: Int,
                @SerialName("comments_count")
                val commentsCount: Int,
                @SerialName("publication_date")
                val publicationDate: Long) {

    val rating: Double by lazy {initRating()}

    private fun initRating(): Double = 1 / (1 + exp((-(favoritesCount/(commentsCount + 1))).toDouble()))

    fun getLocalDate(): LocalDate = LocalDate.ofEpochDay(publicationDate / (24 * 60 * 60))
}