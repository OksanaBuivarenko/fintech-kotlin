package com.fintech.kotlin.service.impl

import com.fintech.kotlin.model.Locations
import com.fintech.kotlin.service.FillStorageService
import com.fintech.kotlin.service.HttpService
import com.fintech.kotlin.service.LocationsService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class FillLocationsStorageServiceImpl @Autowired  constructor(locationsService: LocationsService,
    webClient: WebClient) : FillStorageService, HttpService<Locations?> {

    private val log = KotlinLogging.logger {}

    private val locationsService: LocationsService

    private val webClient: WebClient

    @Value("\${url.locations}")
    lateinit var url: String

    init {
        log.info("Init FillLocationsStorageServiceImpl")
        this.locationsService = locationsService
        this.webClient = webClient
    }

    override fun fillStorage() {
        val locationsList = getListByApi()
        saveToDataSource(locationsList as List<Locations>)
        log.info("Locations storage filed")
    }

    override fun getListByApi(): List<Locations?>? {
        return webClient
            .get()
            .uri(url)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<Locations?>?>() {})
            .block()
    }

    fun saveToDataSource(list: List<Locations>) {
        log.info("Start saving list locations to storage")
        list.forEach(locationsService::save)
        log.info("Finish saving list locations to storage")
    }
}