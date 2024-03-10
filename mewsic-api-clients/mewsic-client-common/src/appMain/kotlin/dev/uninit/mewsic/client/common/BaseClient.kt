package dev.uninit.mewsic.client.common

import dev.uninit.mewsic.client.common.platform.getPlatformHttpClient
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

abstract class BaseClient : Client {
    override val httpClient = getPlatformHttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }

        configure()
    }
}
