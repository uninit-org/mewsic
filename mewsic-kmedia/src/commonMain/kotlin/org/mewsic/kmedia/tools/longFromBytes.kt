package org.mewsic.kmedia.tools

import kotlin.math.min

fun sig(signature: String): Long {
    val bytes = signature.encodeToByteArray()
    return longFromBytes(bytes)
}
fun longFromBytes(bytes: ByteArray): Long {
    var result = 0L
    for (i in 0..min(bytes.size, 8)) {
        result = result shl 8
        result = result or (bytes[i].toLong() and 0xFF)
    }
    return result
}
