package org.mewsic.commons.raw

import org.mewsic.commons.lang.ExperimentalCacheApi
import org.mewsic.commons.raw.api.ByteChunk

@ExperimentalCacheApi
actual open class LinkedByteChunk actual constructor() : ByteChunk {
    actual val head: LinkedByteChunk?
        get() = TODO("Not yet implemented")
    actual val tail: LinkedByteChunk?
        get() = TODO("Not yet implemented")

    actual override fun get(index: Int): Byte {
        TODO("Not yet implemented")
    }

    actual override fun get(range: IntRange): ByteArray {
        TODO("Not yet implemented")
    }

    actual override fun set(index: Int, value: Byte) {
    }

    actual override fun set(range: IntRange, value: ByteArray) {
    }

    actual companion object {
        actual val CHUNK_SIZE: Int
            get() = TODO("Not yet implemented")
    }


}
