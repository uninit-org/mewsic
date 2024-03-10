package dev.uninit.mewsic.client.common.platform

import io.ktor.client.*

expect fun getPlatformHttpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient
