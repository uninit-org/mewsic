package org.mewsic.commons.raw.api

interface ByteChunk {
    operator fun get(index: Int): Byte
    operator fun get(range: IntRange): ByteArray

    operator fun set(index: Int, value: Byte)
    operator fun set(range: IntRange, value: ByteArray)
}
