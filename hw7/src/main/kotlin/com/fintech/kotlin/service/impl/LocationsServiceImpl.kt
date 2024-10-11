package com.fintech.kotlin.service.impl

import com.fintech.kotlin.dto.request.LocationRq
import com.fintech.kotlin.dto.responce.DeleteRs
import com.fintech.kotlin.dto.responce.LocationsRs
import com.fintech.kotlin.exception.ObjectAlreadyExistsException
import com.fintech.kotlin.exception.ObjectNotFoundException
import com.fintech.kotlin.mapper.LocationsMapper
import com.fintech.kotlin.model.Locations
import com.fintech.kotlin.repository.LocationsRepository
import com.fintech.kotlin.service.LocationsService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class LocationsServiceImpl @Autowired @Lazy constructor(
    locationsRepository: LocationsRepository, locationsMapper: LocationsMapper
) : LocationsService {

    private val locationsRepository: LocationsRepository

    private val locationsMapper: LocationsMapper

    init {
        this.locationsRepository = locationsRepository
        this.locationsMapper = locationsMapper
    }

    private val log = KotlinLogging.logger {}

    override fun getLocationsRs(): List<LocationsRs> {
        return getLocations().map { locationsMapper.toDto(it) }
    }

    override fun getLocations(): List<Locations> {
        return locationsRepository.findAll()
    }

    override fun getLocationRsById(id: String): LocationsRs {
        return locationsMapper.toDto(getLocationById(id))
    }

    override fun getLocationById(id: String): Locations {
        return locationsRepository.findById(id) ?: throw ObjectNotFoundException("locations", id)
    }

    override fun createLocation(locationRq: LocationRq): LocationsRs {
        for (el in getLocations()) {
            if (el.slug.equals(locationRq.slug)) {
                throw ObjectAlreadyExistsException("Locations with slug ${el.slug} already exists")
            }
            if (el.name.equals(locationRq.name)) {
                throw ObjectAlreadyExistsException("Locations with name ${el.name} already exists")
            }
        }
        val locations: Locations = locationsMapper.toEntity(locationRq)
        locationsRepository.save(locations.slug.toString(), locations)
        log.debug("Locations with id ${locations.slug} create successfully")
        return locationsMapper.toDto(locations)
    }

    override fun updateLocation(id: String, locationRq: LocationRq): LocationsRs {
        val location: Locations = getLocationById(id)
        if (locationRq.name!!.isNotEmpty() && locationRq.name != location.name) {
            location.name = locationRq.name
            log.debug("Locations with id $id update name")
        }
        if (locationRq.slug!!.isNotEmpty() && locationRq.slug != location.slug) {
            log.warn("Locations slug $id cannot be changed")
        }
        return locationsMapper.toDto(locationsRepository.update(location.slug.toString(), location))
    }

    override fun deleteLocation(id: String): DeleteRs {
        return if (locationsRepository.containsId(id)) {
            locationsRepository.delete(id)
            log.debug("Locations with id $id delete successfully")
            DeleteRs("Locations with id $id delete successfully")
        } else {
            log.warn("Unable to delete locations. Storage don't contains locations with id $id")
            return DeleteRs("Storage don't contains locations with id $id")
        }
    }

    override fun save(locations: Locations) {
        locationsRepository.save(locations.slug.toString(), locations)
    }
}