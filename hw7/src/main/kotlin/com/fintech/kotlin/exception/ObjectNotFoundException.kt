package com.fintech.kotlin.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ObjectNotFoundException(objectType: String?, id: String) :
    RuntimeException("Storage don't contains $objectType with id $id")