package org.mewsic.commons.streams

import org.mewsic.commons.lang.DelicateStreamApi
import org.mewsic.commons.lang.ExperimentalCacheApi
import org.mewsic.commons.raw.LinkedByteChunk
import org.mewsic.commons.raw.api.ByteChunk
import org.mewsic.commons.streams.api.SeekableInputStream
import org.mewsic.commons.streams.api.SeekableOutputStream

/**
 * Import a [LinkedByteChunk] as a [SeekableInputStream], allowing for random access to the stream without sacrificing memory
 *
 * NOTE: [LinkedByteChunk]s have an unlimited size because they will just keep adding chunks to the end of the linked list.
 * Be careful when using this.
 */
@DelicateStreamApi
@OptIn(ExperimentalCacheApi::class)
class LinkedByteChunkStream @OptIn(ExperimentalCacheApi::class) constructor(linkedByteChunk: LinkedByteChunk, var len: Int = -1) : SeekableInputStream, ByteChunk by linkedByteChunk {
    private var position = 0
    override fun read(): Byte {
        return this[position++]
    }

    override fun read(bytes: ByteArray): Int {
        return read(bytes, 0, bytes.size)
    }

    override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
        val read = this[position until position + length]
        read.copyInto(bytes, offset, 0, read.size)
        position += read.size
        return read.size
    }

    override fun readNBytes(n: Int): ByteArray {
        return this[position until position + n].also { position += it.size }
    }

    override fun skip(n: Long): Long {
        position += n.toInt()
        return n
    }

    override fun seek(offset: Long) {
        position = offset.toInt()
    }

    override fun back(offset: Long) {
        position -= offset.toInt()
    }

    override fun position(): Long {
        return position.toLong()
    }

    @Deprecated("This method only showcases an arbitrary value given in constructor or otherwise set, and is thus unreliable.", level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("len.toLong()")
    )
    override fun length(): Long {
        return len.toLong()
    }
}
