package org.mewsic.commons.binary

import org.mewsic.commons.streams.api.SeekableInputStream

open class SeekableBinaryReader(stream: SeekableInputStream, bigEndian: Boolean = true) : BinaryReader(stream, bigEndian) {
    fun seek(offset: Long) {
        (stream as SeekableInputStream).seek(offset)
    }

    fun position(): Long {
        return (stream as SeekableInputStream).position()
    }

    fun length(): Long {
        return (stream as SeekableInputStream).length()
    }
}
