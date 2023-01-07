package org.mewsic.commons.raw.api

interface BinaryCached {
    val id: ByteArray
    fun encode(): ByteArray
    fun decode(data: ByteArray): BinaryCached
}
