package com.fintech.kotlin.mapper

import com.fintech.kotlin.dto.request.CategoriesRq
import com.fintech.kotlin.dto.responce.CategoriesRs
import com.fintech.kotlin.model.Categories
import org.mapstruct.Mapper
import org.mapstruct.Mapping


@Mapper(componentModel = "spring")
interface CategoriesMapper {

        fun toDto(categories: Categories?): CategoriesRs

        @Mapping(target = "id", source = "id")
        fun toEntity(categoriesRq: CategoriesRq, id: Long): Categories

}