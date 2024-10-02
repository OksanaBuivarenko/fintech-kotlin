package com.fintech.kotlin.service

interface HttpService<T> {
    fun getListByApi(): List<T>?
}