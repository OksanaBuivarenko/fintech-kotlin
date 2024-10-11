package com.fintech.kotlin.service.impl

import com.fintech.kotlin.dto.request.LocationRq
import com.fintech.kotlin.dto.responce.DeleteRs
import com.fintech.kotlin.dto.responce.LocationsRs
import com.fintech.kotlin.exception.ObjectAlreadyExistsException
import com.fintech.kotlin.exception.ObjectNotFoundException
import com.fintech.kotlin.mapper.LocationsMapper
import com.fintech.kotlin.model.Locations
import com.fintech.kotlin.repository.LocationsRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`


class LocationsServiceImplTest {
    private val locationsRepository: LocationsRepository = Mockito.mock(LocationsRepository::class.java)
    private val locationsMapper = Mockito.mock(LocationsMapper::class.java)
    private val locationsService: LocationsServiceImpl =  LocationsServiceImpl(locationsRepository, locationsMapper)

    private lateinit var locationsList: List<Locations>
    private lateinit var locations: Locations
    private lateinit var locationsRs: LocationsRs

    @BeforeEach
    fun setUp() {
        locations = Locations("Slug", "Name")
        locationsRs = LocationsRs("Slug", "Name")
        locationsList = ArrayList()
        (locationsList as ArrayList<Locations>).add(locations)
    }

    @Test
    fun getLocationsRsFromNotEmptyRepositorySuccess() {
        `when`(locationsRepository.findAll()).thenReturn(locationsList)
        `when`(locationsMapper.toDto(locations)).thenReturn(locationsRs)

        val result = locationsService.getLocationsRs()

        assertEquals(1, result.size)
        assertEquals(locationsRs, result[0])
        verify(locationsRepository).findAll()
        verify(locationsMapper).toDto(locations)
    }

    @Test
    fun getLocationsRsFromEmptyRepositorySuccess() {
        `when`(locationsRepository.findAll()).thenReturn(ArrayList())

        val result = locationsService.getLocationsRs()

        assertEquals(0, result.size)
        verify(locationsRepository).findAll()
    }

    @Test
    fun getLocationsFromNotEmptyRepositorySuccess() {
        `when`(locationsRepository.findAll()).thenReturn(locationsList)

        val result = locationsService.getLocations()

        assertEquals(1, result.size)
        verify(locationsRepository).findAll()
    }

    @Test
    fun getLocationsFromEmptyRepositorySuccess() {
        `when`(locationsRepository.findAll()).thenReturn(ArrayList())

        val result = locationsService.getLocations()

        assertEquals(0, result.size)
        verify(locationsRepository).findAll()
    }

    @Test
    fun getLocationRsByIdIsPresentSuccess() {
        `when`(locationsRepository.findById("Slug")).thenReturn(locations)
        `when`(locationsMapper.toDto(locations)).thenReturn(locationsRs)

        val result = locationsService.getLocationRsById("Slug")

        assertEquals(locationsRs, result)
        verify(locationsRepository).findById("Slug")
        verify(locationsMapper).toDto(locations)
    }

    @Test
    fun getLocationRsByIdIsNotPresentFail() {
        `when`(locationsRepository.findById("SlugSlug")).thenReturn(null)

        val assert = Assertions.assertThrows(ObjectNotFoundException::class.java) {
            locationsService.getLocationById("SlugSlug")
        }
        assertEquals("Storage don't contains locations with id SlugSlug", assert.message)
    }

    @Test
    fun getLocationByIdIsPresentSuccess() {
        `when`(locationsRepository.findById("Slug")).thenReturn(locations)

        val result = locationsService.getLocationById("Slug")

        assertEquals(locations, result)
        verify(locationsRepository).findById("Slug")
    }

    @Test
    fun getLocationByIdIsNotPresentFail() {
        `when`(locationsRepository.findById("SlugSlug")).thenReturn(null)
        val assert = Assertions.assertThrows(ObjectNotFoundException::class.java) {
            locationsService.getLocationById("SlugSlug")
        }
        assertEquals("Storage don't contains locations with id SlugSlug", assert.message)
    }

    @Test
    fun createLocationWithUniqSlugAndNameSuccess() {
        val locationRq = LocationRq( "Slug2", "Name2")
        val locations2 = Locations( "Slug2", "Name2")
        val locationsRs2 = LocationsRs( "Slug2", "Name2")
        `when`(locationsRepository.findAll()).thenReturn(locationsList)
        `when`(locationsMapper.toEntity(locationRq)).thenReturn(locations2)
        `when`(locationsRepository.save("Slug2", locations2)).thenReturn(locations2)
        `when`(locationsMapper.toDto(locations2)).thenReturn(locationsRs2)

        val result = locationsService.createLocation(locationRq)

        assertEquals(locationsRs2, result)
        verify(locationsRepository).findAll()
        verify(locationsRepository).save("Slug2", locations2)
        verify(locationsMapper).toEntity(locationRq)
        verify(locationsMapper).toDto(locations2)
    }

    @Test
    fun createLocationWithRepeatSlugFail() {
        val locationRq = LocationRq( "Slug", "Name2")
        val locations2 = Locations( "Slug", "Name2")
        val locationsRs2 = LocationsRs( "Slug", "Name2")
        `when`(locationsRepository.findAll()).thenReturn(locationsList)
        `when`(locationsMapper.toEntity(locationRq)).thenReturn(locations2)
        `when`(locationsRepository.save("Slug", locations2)).thenReturn(locations2)
        `when`(locationsMapper.toDto(locations2)).thenReturn(locationsRs2)

        val assert = Assertions.assertThrows(ObjectAlreadyExistsException::class.java) {
            locationsService.createLocation(locationRq)
        }
        assertEquals("Locations with slug Slug already exists", assert.message)
    }

    @Test
    fun createLocationWithRepeatNameFail() {
        val locationRq = LocationRq( "Slug2", "Name")
        val locations2 = Locations( "Slug2", "Name")
        val locationsRs2 = LocationsRs( "Slug2", "Name")
        `when`(locationsRepository.findAll()).thenReturn(locationsList)
        `when`(locationsMapper.toEntity(locationRq)).thenReturn(locations2)
        `when`(locationsRepository.save("Slug", locations2)).thenReturn(locations2)
        `when`(locationsMapper.toDto(locations2)).thenReturn(locationsRs2)

        val assert = Assertions.assertThrows(ObjectAlreadyExistsException::class.java) {
            locationsService.createLocation(locationRq)
        }
        assertEquals("Locations with name Name already exists", assert.message)
    }

    @Test
    fun updateLocationWithIdIsPresentSuccess() {
        val updateLocations = Locations( "Slug", "UpdateName")
        val updateLocationsRs = LocationsRs("Slug", "UpdateName")
        `when`(locationsRepository.findById("Slug")).thenReturn(locations)
        `when`(locationsRepository.update("Slug", updateLocations)).thenReturn(updateLocations)
        `when`(locationsMapper.toDto(updateLocations)).thenReturn(updateLocationsRs)

        val result = locationsService.updateLocation("Slug", LocationRq("Slug", "UpdateName"))

        assertEquals(updateLocationsRs, result)
        verify(locationsRepository).findById("Slug")
        verify(locationsRepository).update("Slug", updateLocations)
        verify(locationsMapper).toDto(updateLocations )
    }

    @Test
    fun updateLocationWithIdIsNotPresentFail() {
        `when`(locationsRepository.findById("SlugSlug")).thenReturn(null)

        val assert = Assertions.assertThrows(ObjectNotFoundException::class.java) {
            locationsService.updateLocation("SlugSlug", LocationRq("SlugSlug", "UpdateName"))
        }
        assertEquals("Storage don't contains locations with id SlugSlug", assert.message)
    }

    @Test
    fun deleteLocationByIdIsPresentSuccess() {
        `when`(locationsRepository.containsId("Slug")).thenReturn(true)

        assertEquals(DeleteRs("Locations with id Slug delete successfully"), locationsService.deleteLocation("Slug"))
    }

    @Test
    fun deleteLocationByIdIsNotPresentFail() {
        `when`(locationsRepository.containsId("SlugSlug")).thenReturn(false)

        assertEquals(DeleteRs("Storage don't contains locations with id Slug"), locationsService.deleteLocation("Slug"))
    }

    @Test
    fun saveLocationsSuccess() {
        locationsService.save(locations)

        verify(locationsRepository).save("Slug", locations)
    }
}