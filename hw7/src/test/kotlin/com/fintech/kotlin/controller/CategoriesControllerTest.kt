package com.fintech.kotlin.controller
import com.fasterxml.jackson.databind.ObjectMapper
import com.fintech.kotlin.dto.request.CategoriesRq
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.wiremock.integrations.testcontainers.WireMockContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class CategoriesControllerTest {

    val mapper: ObjectMapper = ObjectMapper()

    companion object {
        @Container
        @JvmStatic
        val wiremock: WireMockContainer =
            WireMockContainer("wiremock/wiremock:2.35.0")
                .withMappingFromResource("place-categories", "place-categories.json")
                .withMappingFromResource("locations", "locations.json")

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("api.base-url", wiremock::getBaseUrl)
        }
    }

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        wiremock.getUrl("/place-categories/")
        wiremock.getUrl("/locations/")
    }

    @Test
    fun getCategoriesRsSuccess() {
        this.mockMvc.perform(get("http://localhost:$port/api/v1/places/categories"))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.[0].id").value("130"))
            .andExpect(jsonPath("$.[0].slug").value("art-space"))
            .andExpect(jsonPath("$.[0].name").value("Арт-пространства"))
            .andExpect(jsonPath("$.length()").value(54))
    }

    @Test
    fun getCategoryByIdIsPresentSuccess() {
        this.mockMvc.perform(get("http://localhost:$port/api/v1/places/categories/140"))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.id").value("140"))
            .andExpect(jsonPath("$.slug").value("recreation"))
            .andExpect(jsonPath("$.name").value("Активный отдых"))
            .andExpect(jsonPath("$.length()").value(3))
    }

    @Test
    fun getCategoryByIdIsNotPresentFail() {
        this.mockMvc.perform(get("http://localhost:$port/api/v1/places/categories/1400"))
            .andDo(print())
            .andExpectAll(status().isNotFound)
    }

    @Test
    fun createCategoryWithUniqSlugAndNameSuccess() {
        val categoriesRq = CategoriesRq("Slug", "Name")
        this.mockMvc.perform(
            MockMvcRequestBuilders.post("http://localhost:$port/api/v1/places/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(categoriesRq)))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.slug").value("Slug"))
            .andExpect(jsonPath("$.name").value("Name"))
            .andExpect(jsonPath("$.length()").value(3))
    }

    @Test
    fun createCategoryWithRepeatSlugFail() {
        val categoriesRq = CategoriesRq("bar", "Name")
        this.mockMvc.perform(
            MockMvcRequestBuilders.post("http://localhost:$port/api/v1/places/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(categoriesRq)))
            .andDo(print())
            .andExpectAll(status().isConflict)
    }

    @Test
    fun createCategoryWithRepeatNameFail() {
        val categoriesRq = CategoriesRq("Slug", "Бары и пабы")
        this.mockMvc.perform(
            MockMvcRequestBuilders.post("http://localhost:$port/api/v1/places/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(categoriesRq)))
            .andDo(print())
            .andExpectAll(status().isConflict)
    }

    @Test
    fun updateCategoryWithIdIsPresentSuccess() {
        val categoriesRq = CategoriesRq("UpdateSlug", "UpdateName")
        this.mockMvc.perform(
            MockMvcRequestBuilders.put("http://localhost:$port/api/v1/places/categories/17")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(categoriesRq)))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.slug").value("UpdateSlug"))
            .andExpect(jsonPath("$.name").value("UpdateName"))
            .andExpect(jsonPath("$.length()").value(3))
    }

    @Test
    fun updateCategoryWithIdIsNotPresentFail() {
        val categoriesRq = CategoriesRq("UpdateSlug", "UpdateName")
        this.mockMvc.perform(
            MockMvcRequestBuilders.put("http://localhost:$port/api/v1/places/categories/1700")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(categoriesRq)))
            .andDo(print())
            .andExpectAll(status().isNotFound)
    }

    @Test
    fun deleteCategoryByIdIsPresentSuccess() {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:$port/api/v1/places/categories/15"))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.message").value("Categories with id 15 delete successfully"))
            .andExpect(jsonPath("$.length()").value(1))
    }

    @Test
    fun deleteCategoryByIdIsNotPresentFail() {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:$port/api/v1/places/categories/1500"))
            .andDo(print())
            .andExpectAll(status().isOk)
            .andExpect(jsonPath("$.message").value("Storage don't contains categories with id 1500"))
            .andExpect(jsonPath("$.length()").value(1))
    }
}