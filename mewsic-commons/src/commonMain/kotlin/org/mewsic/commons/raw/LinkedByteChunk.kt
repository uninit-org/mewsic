package org.mewsic.commons.raw

import org.mewsic.commons.lang.ExperimentalCacheApi
import org.mewsic.commons.raw.api.ByteChunk


@ExperimentalCacheApi
expect open class LinkedByteChunk() : ByteChunk {
    val head: LinkedByteChunk?
    val tail: LinkedByteChunk?
    override operator fun get(index: Int): Byte
    override operator fun get(range: IntRange): ByteArray

    override operator fun set(index: Int, value: Byte)
    override operator fun set(range: IntRange, value: ByteArray)


    companion object {
        val CHUNK_SIZE: Int // 256 KB
    }

}
