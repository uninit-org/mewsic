package org.mewsic.commons.streams.api

interface SeekableInputStream : InputStream, Seekable {
    operator fun get(position: Long): Byte
    operator fun get(range: ULongRange): ByteArray
    operator fun get(range: LongRange): ByteArray
    operator fun get(range: IntRange): ByteArray
}
