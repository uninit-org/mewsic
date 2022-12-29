package org.mewsic.commons.streams

import org.mewsic.commons.streams.api.OutputStream

typealias JOutputStream = java.io.OutputStream

open class JavaOutputStream(private val stream: JOutputStream) : OutputStream {
    override fun write(b: Byte) = stream.write(b.toInt())

    override fun write(bytes: ByteArray) = stream.write(bytes)

    override fun write(bytes: ByteArray, offset: Int, length: Int) = stream.write(bytes, offset, length)

    override fun skip(n: Long): Long {
        stream.write(ByteArray(n.toInt()))
        return n
    }
}
