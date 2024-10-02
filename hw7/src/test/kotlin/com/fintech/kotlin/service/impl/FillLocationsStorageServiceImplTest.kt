package com.fintech.kotlin.service.impl

import com.fintech.kotlin.model.Locations
import com.fintech.kotlin.service.LocationsService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.web.reactive.function.client.WebClient

class FillLocationsStorageServiceImplTest {
    private val locationsService = Mockito.mock(LocationsService::class.java)

    private val webClient: WebClient = Mockito.mock(WebClient::class.java)

    private val fillLocationsStorageServiceImpl =
        FillLocationsStorageServiceImpl(locationsService, webClient)

    @Test
    fun saveToDataSource() {
        val list = ArrayList<Locations>()
        val locations = Locations("Slug", "Name")
        list.add(locations)

        fillLocationsStorageServiceImpl.saveToDataSource(list)

        Mockito.verify(locationsService).save(locations)
    }
}