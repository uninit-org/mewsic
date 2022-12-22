package org.mewsic.commons.streams

import org.mewsic.commons.streams.api.SeekableInputStream

class ByteArrayInputStream(private val array: ByteArray) : SeekableInputStream {
    private var offset = 0

    override fun read() = array[offset++]

    override fun read(bytes: ByteArray): Int {
        val length = bytes.size
        val remaining = array.size - offset
        if (remaining < length) throw EndOfStreamException()
        array.copyInto(bytes, 0, offset, offset + length)
        offset += length
        return length
    }

    override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
        val remaining = array.size - this.offset
        if (remaining < length) throw EndOfStreamException()
        array.copyInto(bytes, offset, this.offset, this.offset + length)
        this.offset += length
        return length
    }

    override fun readNBytes(n: Int): ByteArray {
        val array = ByteArray(n)
        read(array)
        return array
    }

    override fun skip(n: Long): Long {
        val remaining = (array.size - offset).toLong()
        val skipped = if (n > remaining) remaining else n
        offset += skipped.toInt()
        return skipped
    }

    override fun seek(offset: Long) {
        this.offset = offset.toInt()
    }

    override fun position(): Long {
        return offset.toLong()
    }

    override fun length(): Long {
        return array.size.toLong()
    }
}
