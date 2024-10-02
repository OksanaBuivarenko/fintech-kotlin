package com.fintech.kotlin.mapper

import com.fintech.kotlin.dto.responce.LocationsRs
import com.fintech.kotlin.dto.request.LocationRq
import com.fintech.kotlin.model.Locations
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface LocationsMapper {

    fun toDto(locations: Locations?): LocationsRs

    fun toEntity(locationRq: LocationRq?): Locations
}