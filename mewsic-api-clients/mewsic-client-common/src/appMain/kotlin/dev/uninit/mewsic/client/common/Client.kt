package dev.uninit.mewsic.client.common

import io.ktor.client.*

interface Client {
    val httpClient: HttpClient

    fun HttpClientConfig<*>.configure() {}
    suspend fun initialSetup() {}
}
