package com.fintech.kotlin.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fintech.kotlin.dto.request.LocationRq
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LocationsControllerTest {

    val mapper: ObjectMapper = ObjectMapper()

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun getLocationSuccess() {
        this.mockMvc.perform(get("http://localhost:$port/api/v1/locations"))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.[0].slug").value("msk"))
            .andExpect(jsonPath("$.[0].name").value("Москва"))
            .andExpect(jsonPath("$.length()").value(5))
    }

    @Test
    fun getLocationByIdIdIsPresentSuccess() {
        this.mockMvc.perform(get("http://localhost:$port/api/v1/locations/spb"))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.slug").value("spb"))
            .andExpect(jsonPath("$.name").value("Санкт-Петербург"))
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun getLocationByIdIdIsNotPresentFail() {
        this.mockMvc.perform(get("http://localhost:$port/api/v1/locations/spbspb"))
            .andDo(print())
            .andExpectAll(status().isNotFound)
    }

    @Test
    fun createLocationWithUniqSlugAndNameSuccess() {
        val locationRq = LocationRq("Slug", "Name")
        this.mockMvc.perform(post("http://localhost:$port/api/v1/locations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(locationRq)))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.slug").value("Slug"))
            .andExpect(jsonPath("$.name").value("Name"))
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun createLocationWithRepeatSlugFail() {
        val locationRq = LocationRq("spb", "Name")
        this.mockMvc.perform(post("http://localhost:$port/api/v1/locations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(locationRq)))
            .andDo(print())
            .andExpectAll(status().isConflict)
    }

    @Test
    fun createLocationWithRepeatNameFail() {
        val locationRq = LocationRq("spb", "Санкт-Петербург")
        this.mockMvc.perform(post("http://localhost:$port/api/v1/locations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(locationRq)))
            .andDo(print())
            .andExpectAll(status().isConflict)
    }

    @Test
    fun updateLocationWithIdIsPresentSuccess() {
        val locationRq = LocationRq("spb", "UpdateName")
        this.mockMvc.perform(
            put("http://localhost:$port/api/v1/locations/spb")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(locationRq)))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.slug").value("spb"))
            .andExpect(jsonPath("$.name").value("UpdateName"))
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun updateLocationWithIdIsNotPresentFail() {
        val locationRq = LocationRq("UpdateSlug", "UpdateName")
        this.mockMvc.perform(
            put("http://localhost:$port/api/v1/locations/spbspb")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(locationRq)))
            .andDo(print())
            .andExpectAll(status().isNotFound)
    }

    @Test
    fun deleteLocationByIdIsPresentSuccess() {
        this.mockMvc.perform(delete("http://localhost:$port/api/v1/locations/ekb"))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.message").value("Locations with id ekb delete successfully"))
            .andExpect(jsonPath("$.length()").value(1))
    }

    @Test
    fun deleteLocationByIdIsNotPresentFail() {
        this.mockMvc.perform(delete("http://localhost:$port/api/v1/locations/ekbekb"))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.message").value("Storage don't contains locations with id ekbekb"))
            .andExpect(jsonPath("$.length()").value(1))
    }
}