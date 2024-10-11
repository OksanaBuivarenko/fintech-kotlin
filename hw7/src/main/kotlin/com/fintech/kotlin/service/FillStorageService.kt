package com.fintech.kotlin.service

import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener

interface FillStorageService {
    @EventListener(ContextRefreshedEvent::class)
    fun fillStorage()
}