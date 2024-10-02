package com.fintech.kotlin.service.impl

import com.fintech.kotlin.dto.request.CategoriesRq
import com.fintech.kotlin.dto.responce.CategoriesRs
import com.fintech.kotlin.dto.responce.DeleteRs
import com.fintech.kotlin.exception.ObjectAlreadyExistsException
import com.fintech.kotlin.exception.ObjectNotFoundException
import com.fintech.kotlin.mapper.CategoriesMapper
import com.fintech.kotlin.model.Categories
import com.fintech.kotlin.repository.CategoriesRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class CategoriesServiceImplTest {

    private val categoriesRepository: CategoriesRepository = Mockito.mock(CategoriesRepository::class.java)
    private val categoriesMapper: CategoriesMapper = Mockito.mock(CategoriesMapper::class.java)
    private val categoriesService: CategoriesServiceImpl = CategoriesServiceImpl(categoriesRepository, categoriesMapper)

    private lateinit var categoriesList: List<Categories>
    private lateinit var categories: Categories
    private lateinit var categoriesRs: CategoriesRs

    @BeforeEach
    fun setUp() {
        categories = Categories(1, "Slug", "Name")
        categoriesRs = CategoriesRs(1, "Slug", "Name")
        categoriesList = ArrayList()
        (categoriesList as ArrayList<Categories>).add(categories)
    }

    @Test
    fun getCategoriesFromNotEmptyRepositorySuccess() {
        `when`(categoriesRepository.findAll()).thenReturn(categoriesList)

        val result = categoriesService.getCategories()

        assertEquals(1, result.size)
        verify(categoriesRepository).findAll()
    }

    @Test
    fun getCategoriesFromEmptyRepositorySuccess() {
        `when`(categoriesRepository.findAll()).thenReturn(ArrayList<Categories>())

        val result = categoriesService.getCategories()

        assertEquals(0, result.size)
        verify(categoriesRepository).findAll()
    }

    @Test
    fun getCategoriesRsFromNotEmptyRepositorySuccess() {
        `when`(categoriesRepository.findAll()).thenReturn(categoriesList)
        `when`(categoriesMapper.toDto(categories)).thenReturn(categoriesRs)

        val result = categoriesService.getCategoriesRs()

        assertEquals(1, result.size)
        assertEquals(categoriesRs, result[0])
        verify(categoriesRepository).findAll()
        verify(categoriesMapper).toDto(categories)
    }

    @Test
    fun getCategoriesRsFromEmptyRepositorySuccess() {
        `when`(categoriesRepository.findAll()).thenReturn(ArrayList<Categories>())

        val result = categoriesService.getCategoriesRs()

        assertEquals(0, result.size)
        verify(categoriesRepository).findAll()
    }

    @Test
    fun getCategoryByIdIsPresentSuccess() {
        `when`(categoriesRepository.findById(1)).thenReturn(categories)

        val result = categoriesService.getCategoryById(1)

        assertEquals(categories, result)
        verify(categoriesRepository).findById(1)
    }

    @Test
    fun getCategoryByIdIsNotPresentFail() {
        `when`(categoriesRepository.findById(100)).thenReturn(null)

        val assert = assertThrows(ObjectNotFoundException::class.java) {
            categoriesService.getCategoryById(100)
        }
        assertEquals("Storage don't contains categories with id 100", assert.message)
    }

    @Test
    fun getCategoryRsByIdIsPresentSuccess() {
        `when`(categoriesRepository.findById(1)).thenReturn(categories)
        `when`(categoriesMapper.toDto(categories)).thenReturn(categoriesRs)

        val result = categoriesService.getCategoryRsById(1)

        assertEquals(categoriesRs, result)
        verify(categoriesRepository).findById(1)
        verify(categoriesMapper).toDto(categories)
    }

    @Test
    fun getCategoryRsByIdIsNotPresentFail() {
        `when`(categoriesRepository.findById(100)).thenReturn(null)

        val assert = assertThrows(ObjectNotFoundException::class.java) {
            categoriesService.getCategoryById(100)
        }
        assertEquals("Storage don't contains categories with id 100", assert.message)
    }

    @Test
    fun createCategoryWithUniqSlugAndNameSuccess() {
        val categoriesRq = CategoriesRq("Slug2", "Name2")
        val categories2 = Categories(2, "Slug2", "Name2")
        val categoriesRs2 = CategoriesRs(2, "Slug2", "Name2")
        `when`(categoriesRepository.findAll()).thenReturn(categoriesList)
        `when`(categoriesMapper.toEntity(categoriesRq, 2)).thenReturn(categories2)
        `when`(categoriesRepository.save(2, categories2)).thenReturn(categories2)
        `when`(categoriesMapper.toDto(categories2)).thenReturn(categoriesRs2)
        `when`(categoriesRepository.maxId).thenReturn(2)

        val result = categoriesService.createCategory(categoriesRq)

        assertEquals(categoriesRs2, result)
        verify(categoriesRepository).findAll()
        verify(categoriesRepository).save(2, categories2)
        verify(categoriesMapper).toEntity(categoriesRq, 2)
        verify(categoriesMapper).toDto(categories2)
    }

    @Test
    fun createCategoryWithRepeatSlugFail() {
        val categoriesRq = CategoriesRq("Slug", "Name2")
        val categories2 = Categories(2, "Slug", "Name2")
        val categoriesRs2 = CategoriesRs(2, "Slug", "Name2")
        `when`(categoriesRepository.findAll()).thenReturn(categoriesList)
        `when`(categoriesMapper.toEntity(categoriesRq, 2)).thenReturn(categories2)
        `when`(categoriesRepository.save(2, categories2)).thenReturn(categories2)
        `when`(categoriesMapper.toDto(categories2)).thenReturn(categoriesRs2)
        `when`(categoriesRepository.maxId).thenReturn(2)

        val assert = assertThrows(ObjectAlreadyExistsException::class.java) {
            categoriesService.createCategory(categoriesRq)
        }
        assertEquals("Categories with slug Slug already exists", assert.message)
    }

    @Test
    fun createCategoryWithRepeatNameFail() {
        val categoriesRq = CategoriesRq("Slug2", "Name")
        val categories2 = Categories(2, "Slug2", "Name")
        val categoriesRs2 = CategoriesRs(2, "Slug2", "Name")
        `when`(categoriesRepository.findAll()).thenReturn(categoriesList)
        `when`(categoriesMapper.toEntity(categoriesRq, 2)).thenReturn(categories2)
        `when`(categoriesRepository.save(2, categories2)).thenReturn(categories2)
        `when`(categoriesMapper.toDto(categories2)).thenReturn(categoriesRs2)
        `when`(categoriesRepository.maxId).thenReturn(2)

        val assert = assertThrows(ObjectAlreadyExistsException::class.java) {
            categoriesService.createCategory(categoriesRq)
        }
        assertEquals("Categories with name Name already exists", assert.message)
    }

    @Test
    fun updateCategoryWithIdIsPresentSuccess() {
        val updateCategories = Categories(1, "UpdateSlug", "UpdateName")
        val updateCategoriesRs = CategoriesRs(1, "UpdateSlug", "UpdateName")
        `when`(categoriesRepository.findById(1)).thenReturn(updateCategories)
        `when`(categoriesRepository.update(1, updateCategories)).thenReturn(updateCategories)
        `when`(categoriesMapper.toDto(updateCategories)).thenReturn(updateCategoriesRs)

        val result = categoriesService.updateCategory(1, CategoriesRq("UpdateSlug", "UpdateName"))

        assertEquals(updateCategoriesRs, result)
        verify(categoriesRepository).findById(1)
        verify(categoriesRepository).update(1, updateCategories)
        verify(categoriesMapper).toDto(updateCategories)
    }

    @Test
    fun updateCategoryWithIdIsPresentAndSlugIsEmptySuccess() {
        val updateCategories = Categories(1, "", "UpdateName")
        val updateCategoriesRs = CategoriesRs(1, "", "UpdateName")
        `when`(categoriesRepository.findById(1)).thenReturn(updateCategories)
        `when`(categoriesRepository.update(1, updateCategories)).thenReturn(updateCategories)
        `when`(categoriesMapper.toDto(updateCategories)).thenReturn(updateCategoriesRs)

        val result = categoriesService.updateCategory(1, CategoriesRq("", "UpdateName"))

        assertEquals(updateCategoriesRs, result)
        verify(categoriesRepository).findById(1)
        verify(categoriesRepository).update(1, updateCategories)
        verify(categoriesMapper).toDto(updateCategories)
    }

    @Test
    fun updateCategoryWithIdIsPresentAndNameIsEmptySuccess() {
        val updateCategories = Categories(1, "Slug", "")
        val updateCategoriesRs = CategoriesRs(1, "Slug", "")
        `when`(categoriesRepository.findById(1)).thenReturn(updateCategories)
        `when`(categoriesRepository.update(1, updateCategories)).thenReturn(updateCategories)
        `when`(categoriesMapper.toDto(updateCategories)).thenReturn(updateCategoriesRs)

        val result = categoriesService.updateCategory(1, CategoriesRq("UpdateSlug", ""))

        assertEquals(updateCategoriesRs, result)
        verify(categoriesRepository).findById(1)
        verify(categoriesRepository).update(1, updateCategories)
        verify(categoriesMapper).toDto(updateCategories)
    }

    @Test
    fun updateCategoryWithIdIsNotPresentFail() {
        val updateCategories = Categories(100, "UpdateSlug", "UpdateName")
        val updateCategoriesRs = CategoriesRs(100, "UpdateSlug", "UpdateName")
        `when`(categoriesRepository.findById(100)).thenReturn(null)
        `when`(categoriesRepository.update(100, updateCategories)).thenReturn(updateCategories)
        `when`(categoriesMapper.toDto(updateCategories)).thenReturn(updateCategoriesRs)

        val assert = assertThrows(ObjectNotFoundException::class.java) {
            categoriesService.updateCategory(100, CategoriesRq("UpdateSlug", "UpdateName"))
        }
        assertEquals("Storage don't contains categories with id 100", assert.message)
    }

    @Test
    fun deleteCategoryByIdIsPresentSuccess() {
        `when`(categoriesRepository.containsId(1)).thenReturn(true)

        assertEquals(DeleteRs("Categories with id 1 delete successfully"), categoriesService.deleteCategory(1))
    }

    @Test
    fun deleteCategoryByIdIsNotPresentFail() {
        `when`(categoriesRepository.containsId(5)).thenReturn(false)

        assertEquals(DeleteRs("Storage don't contains categories with id 5"), categoriesService.deleteCategory(5))
    }

    @Test
    fun saveLocationSuccess() {
        categoriesService.save(categories)

        verify(categoriesRepository).save(categories.id, categories)
    }

    @Test
    fun setMaxId() {
        categoriesService.setMaxId(5)

        verify(categoriesRepository).maxId = 5
    }
}