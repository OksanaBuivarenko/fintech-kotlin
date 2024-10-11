package com.fintech.kotlin.service.impl

import com.fintech.kotlin.dto.request.CategoriesRq
import com.fintech.kotlin.dto.responce.CategoriesRs
import com.fintech.kotlin.dto.responce.DeleteRs
import com.fintech.kotlin.exception.ObjectAlreadyExistsException
import com.fintech.kotlin.exception.ObjectNotFoundException
import com.fintech.kotlin.mapper.CategoriesMapper
import com.fintech.kotlin.model.Categories
import com.fintech.kotlin.repository.CategoriesRepository
import com.fintech.kotlin.service.CategoriesService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class CategoriesServiceImpl @Autowired constructor(categoriesRepository: CategoriesRepository, categoriesMapper: CategoriesMapper ) : CategoriesService {

    private val categoriesRepository: CategoriesRepository

    private val categoriesMapper: CategoriesMapper

    init {
        this.categoriesRepository = categoriesRepository
        this.categoriesMapper = categoriesMapper
    }

    private val log = KotlinLogging.logger {}

    override fun getCategories(): List<Categories?> {
        return categoriesRepository.findAll()
    }

    override fun getCategoriesRs(): List<CategoriesRs> {
        return getCategories().map { categoriesMapper.toDto(it) }
    }

    override fun getCategoryById(id: Long): Categories {
        return categoriesRepository.findById(id) ?: throw ObjectNotFoundException("categories", id.toString())
    }

    override fun getCategoryRsById(id: Long): CategoriesRs {
        return categoriesMapper.toDto(getCategoryById(id))
    }

    override fun createCategory(categoriesRq: CategoriesRq): CategoriesRs {
        for (el in getCategories()) {
            if (el?.slug.equals(categoriesRq.slug)) {
                throw ObjectAlreadyExistsException("Categories with slug ${el?.slug} already exists")
            }
            if (el?.name.equals(categoriesRq.name)) {
               throw ObjectAlreadyExistsException("Categories with name ${el?.name} already exists")
            }
        }
        categoriesRepository.incrementMaxId()
        val categories: Categories = categoriesMapper.toEntity(categoriesRq, categoriesRepository.maxId)
        val saveCategory = categoriesRepository.save(categories.id, categories)
        return categoriesMapper.toDto(saveCategory)
    }

    override fun updateCategory(id: Long, categoriesRq: CategoriesRq): CategoriesRs {
        val categories = getCategoryById(id)
        if (categoriesRq.name!!.isNotEmpty() && categoriesRq.name != categories.name) {
            categories.name = categoriesRq.name
            log.debug("Categories with id $id update name")
        }
        if (categoriesRq.slug!!.isNotEmpty() && categoriesRq.slug != categories.slug) {
            categories.slug = categoriesRq.slug
            log.debug("Categories with id $id update slug")
        }
        return categoriesMapper.toDto(categoriesRepository.update(categories.id, categories))
    }

    override fun deleteCategory(id: Long): DeleteRs {
        return if (categoriesRepository.containsId(id)) {
            categoriesRepository.delete(id)
            log.info("Categories with id $id delete successfully")
            DeleteRs("Categories with id $id delete successfully")

        } else {
            log.warn("Unable to delete categories. Storage don't contains categories with id $id")
            return DeleteRs("Storage don't contains categories with id $id")
        }
    }

    override fun save(categories: Categories) {
        categoriesRepository.save(categories.id, categories)
    }

    override fun setMaxId(id: Long) {
        categoriesRepository.maxId = id
    }
}