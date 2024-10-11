package com.fintech.kotlin.service.impl

import com.fintech.kotlin.model.Categories
import com.fintech.kotlin.service.CategoriesService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.springframework.web.reactive.function.client.WebClient


class FillCategoriesStorageServiceImplTest {

    private val categoriesService: CategoriesService = Mockito.mock(CategoriesService::class.java)

    private val webClient: WebClient = Mockito.mock(WebClient::class.java)

    private val fillCategoriesStorageServiceImpl: FillCategoriesStorageServiceImpl =
        FillCategoriesStorageServiceImpl(categoriesService, webClient)

    @Test
    fun saveToDataSource() {
        val list = ArrayList<Categories>()
        val categories = Categories(1, "Slug", "Name")
        list.add(categories)

        fillCategoriesStorageServiceImpl.saveToDataSource(list)

        verify(categoriesService).save(categories)
        verify(categoriesService).setMaxId(1)
    }
}


