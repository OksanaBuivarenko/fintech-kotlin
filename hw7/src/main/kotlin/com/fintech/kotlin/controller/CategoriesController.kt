package com.fintech.kotlin.controller

import com.fintech.kotlin.dto.request.CategoriesRq
import com.fintech.kotlin.dto.responce.CategoriesRs
import com.fintech.kotlin.dto.responce.DeleteRs
import com.fintech.kotlin.service.CategoriesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/places/categories")
class CategoriesController @Autowired constructor (categoriesService: CategoriesService) {

    private val categoriesService: CategoriesService

    init {
        this.categoriesService = categoriesService
    }

    @GetMapping
    fun getCategoriesRs(): List<CategoriesRs?> {
        return categoriesService.getCategoriesRs()
    }

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Long): CategoriesRs? {
        return categoriesService.getCategoryRsById(id)
    }

    @PostMapping
    fun createCategory(@RequestBody categoriesRq: CategoriesRq): CategoriesRs? {
        return categoriesService.createCategory(categoriesRq)
    }

    @PutMapping("/{id}")
    fun updateCategory(@PathVariable id: Long, @RequestBody categoriesRq: CategoriesRq): CategoriesRs {
        return categoriesService.updateCategory(id, categoriesRq)
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): DeleteRs? {
        return categoriesService.deleteCategory(id)
    }
}