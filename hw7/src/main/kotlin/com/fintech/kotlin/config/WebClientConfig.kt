package com.fintech.kotlin.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig {
    @Value("\${url.base_url}")
    lateinit var url: String

    @Bean
    fun WebClient(): WebClient {
        return WebClient.builder().baseUrl(url)
            .clientConnector(ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
            .build()
    }
}