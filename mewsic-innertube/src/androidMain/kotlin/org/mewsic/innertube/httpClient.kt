package org.mewsic.innertube

import io.ktor.client.*
import io.ktor.client.engine.android.*

actual fun httpClient(): HttpClient {
    return HttpClient(Android)
}
