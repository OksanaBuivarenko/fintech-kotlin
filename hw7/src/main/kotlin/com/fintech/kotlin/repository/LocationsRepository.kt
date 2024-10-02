package com.fintech.kotlin.repository

import com.fintech.kotlin.model.Locations
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class LocationsRepository : AppRepository<String, Locations>() {
    private val log = KotlinLogging.logger {}

    init {
        log.info("Init LocationsRepository")
    }
}