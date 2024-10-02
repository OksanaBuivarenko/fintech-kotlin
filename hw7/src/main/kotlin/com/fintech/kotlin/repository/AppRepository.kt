package com.fintech.kotlin.repository

import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
abstract class AppRepository<K, V> {

    private val appStorage: ConcurrentHashMap<K, V> = ConcurrentHashMap()

    fun findAll(): List<V> {
        return ArrayList<V>(appStorage.values)
    }

    fun findById(key: K): V? {
        return appStorage[key]
    }

    fun save(key: K, value: V): V? {
       appStorage[key] = value
       return appStorage[key]
    }

    fun update(key: K, value: V): V? {
        return appStorage.computeIfPresent(key){_, _ -> value}
    }

    fun delete(key: K) {
        appStorage.remove(key)
    }

    fun containsId(key: K): Boolean {
        return appStorage.containsKey(key)
    }
}