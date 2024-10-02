package com.fintech.kotlin.controller

import com.fintech.kotlin.dto.request.LocationRq
import com.fintech.kotlin.dto.responce.DeleteRs
import com.fintech.kotlin.dto.responce.LocationsRs
import com.fintech.kotlin.service.LocationsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/locations")
class LocationsController @Autowired constructor(locationsService: LocationsService) {

    private val locationsService: LocationsService

    init {
        this.locationsService = locationsService
    }

    @GetMapping
    fun getLocation(): List<LocationsRs?> {
        return locationsService.getLocationsRs()
    }

    @GetMapping("/{id}")
    fun getLocationById(@PathVariable id: String): LocationsRs? {
        return locationsService.getLocationRsById(id)
    }

    @PostMapping
    fun createLocation(@RequestBody locationRq: LocationRq): LocationsRs? {
        return locationsService.createLocation(locationRq)
    }

    @PutMapping("/{id}")
    fun updateLocation(@PathVariable id: String, @RequestBody locationRq: LocationRq): LocationsRs? {
        return locationsService.updateLocation(id, locationRq)
    }

    @DeleteMapping("/{id}")
    fun deleteLocation(@PathVariable id: String): DeleteRs? {
        return locationsService.deleteLocation(id)
    }
}