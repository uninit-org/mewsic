package org.mewsic.commons.streams.api

interface OutputStream {
    fun write(b: Byte)
    fun write(bytes: ByteArray)
    fun write(bytes: ByteArray, offset: Int, length: Int)
    fun skip(n: Long): Long //FIXME: is there a reason we have skip on output?
}
