package org.mewsic.commons.binary

import org.mewsic.commons.streams.api.SeekableOutputStream

open class SeekableBinaryWriter(stream: SeekableOutputStream, littleEndian: Boolean = true) : BinaryWriter(stream, littleEndian) {
    fun seek(offset: Long) {
        (stream as SeekableOutputStream).seek(offset)
    }

    fun position(): Long {
        return (stream as SeekableOutputStream).position()
    }

    fun length(): Long {
        return (stream as SeekableOutputStream).length()
    }
}
