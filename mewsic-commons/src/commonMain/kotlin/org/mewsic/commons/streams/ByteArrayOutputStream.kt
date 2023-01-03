package org.mewsic.commons.streams

import org.mewsic.commons.streams.api.SeekableOutputStream

class ByteArrayOutputStream(private val array: ByteArray) : SeekableOutputStream {
    constructor() : this(ByteArray(0))

    private var offset = 0

    override fun write(b: Byte) {
        array[offset++] = b
    }

    override fun write(bytes: ByteArray) {
        bytes.copyInto(array, offset)
        offset += bytes.size
    }

    override fun write(bytes: ByteArray, offset: Int, length: Int) {
        bytes.copyInto(array, this.offset, offset, offset + length)
        this.offset += length
    }

    override fun seek(offset: Long) {
        this.offset = offset.toInt()
    }

    override fun back(offset: Long) {
        this.offset -= offset.toInt()
    }

    override fun position(): Long {
        return offset.toLong()
    }

    override fun length(): Long {
        return array.size.toLong()
    }

    override fun skip(n: Long): Long {
        val remaining = (array.size - offset).toLong()
        val skipped = if (n > remaining) remaining else n
        offset += skipped.toInt()
        return skipped
    }

    fun toByteArray(): ByteArray {
        return array.copyOfRange(0, offset)
    }
}
