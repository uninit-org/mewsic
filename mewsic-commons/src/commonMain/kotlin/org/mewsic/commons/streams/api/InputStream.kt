package org.mewsic.commons.streams.api

interface InputStream {
    fun read(): Byte
    fun read(bytes: ByteArray): Int
    fun read(bytes: ByteArray, offset: Int, length: Int): Int
    fun readNBytes(n: Int): ByteArray
    fun skip(n: Long): Long
}
