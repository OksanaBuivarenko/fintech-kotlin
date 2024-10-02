package com.fintech.kotlin.service.impl

import com.fintech.kotlin.model.Categories
import com.fintech.kotlin.service.CategoriesService
import com.fintech.kotlin.service.FillStorageService
import com.fintech.kotlin.service.HttpService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class FillCategoriesStorageServiceImpl @Autowired  constructor(categoriesService: CategoriesService,
                                                                    webClient: WebClient) : FillStorageService,
    HttpService<Categories?> {

    private val log = KotlinLogging.logger {}

    private val categoriesService: CategoriesService

    private val webClient: WebClient

    @Value("\${url.categories}")
    lateinit var url: String

    init {
        log.info("Init FillCategoriesStorageServiceImpl")
        this.categoriesService = categoriesService
        this.webClient = webClient
    }

    override fun fillStorage() {
        val categoriesList: List<Categories?>? = getListByApi()
        saveToDataSource(categoriesList)
        log.info("Categories storage filed")
    }

     fun saveToDataSource(list: List<Categories?>?) {
        log.info("Start saving list categories to storage")
        list?.forEach { categoriesService.save(it!!)}
        list?.sortedBy { it?.id }?.asReversed()
        categoriesService.setMaxId(list?.get(0)?.id!!)
        log.info("Finish saving list categories to storage")
    }

    override fun getListByApi(): List<Categories?>? {
        return webClient
            .get()
            .uri(url)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<Categories?>?>() {})
            .block()
    }
}