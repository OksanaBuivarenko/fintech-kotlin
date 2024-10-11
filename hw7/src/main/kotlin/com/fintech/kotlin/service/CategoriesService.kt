package com.fintech.kotlin.service

import com.fintech.kotlin.dto.request.CategoriesRq
import com.fintech.kotlin.dto.responce.CategoriesRs
import com.fintech.kotlin.dto.responce.DeleteRs
import com.fintech.kotlin.model.Categories

interface CategoriesService {
    fun getCategories(): List<Categories?>
    fun getCategoriesRs(): List<CategoriesRs>
    fun getCategoryById(id: Long): Categories
    fun getCategoryRsById(id: Long): CategoriesRs
    fun createCategory(categoriesRq: CategoriesRq): CategoriesRs
    fun updateCategory(id: Long, categoriesRq: CategoriesRq): CategoriesRs
    fun deleteCategory(id: Long): DeleteRs
    fun save(categories: Categories)
    fun setMaxId(id: Long)
}