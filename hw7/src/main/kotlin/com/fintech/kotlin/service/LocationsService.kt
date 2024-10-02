package com.fintech.kotlin.service

import com.fintech.kotlin.dto.request.LocationRq
import com.fintech.kotlin.dto.responce.DeleteRs
import com.fintech.kotlin.dto.responce.LocationsRs
import com.fintech.kotlin.model.Locations

interface LocationsService {
    fun getLocations(): List<Locations>
    fun getLocationsRs(): List<LocationsRs>
    fun getLocationRsById(id: String): LocationsRs
    fun createLocation(locationRq: LocationRq): LocationsRs
    fun updateLocation(id: String, locationRq: LocationRq): LocationsRs
    fun deleteLocation(id: String): DeleteRs
    fun save(locations: Locations)
    fun getLocationById(id: String): Locations
}