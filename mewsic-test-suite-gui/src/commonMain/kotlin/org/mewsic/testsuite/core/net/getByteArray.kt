package org.mewsic.testsuite.core.net

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.core.*

suspend fun getByteArray(url: String): ByteArray {
    HttpClient().use {
        return it.get(url).readBytes()
    }
}
