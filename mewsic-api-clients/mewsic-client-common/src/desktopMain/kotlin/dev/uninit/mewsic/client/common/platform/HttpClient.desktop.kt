package dev.uninit.mewsic.client.common.platform

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual fun getPlatformHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(CIO) {
        config()
    }
}
