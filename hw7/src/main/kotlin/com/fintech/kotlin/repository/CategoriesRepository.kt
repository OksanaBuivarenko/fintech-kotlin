package com.fintech.kotlin.repository

import com.fintech.kotlin.model.Categories
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CategoriesRepository : AppRepository<Long?, Categories?>() {
    private val log = KotlinLogging.logger {}

    var maxId: Long = 0

    init {
        log.info("Init CategoriesRepository")
    }

    fun incrementMaxId() {
        maxId += 1
    }
}