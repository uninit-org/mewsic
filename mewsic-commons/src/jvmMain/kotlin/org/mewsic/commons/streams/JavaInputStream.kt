package org.mewsic.commons.streams

import org.mewsic.commons.streams.api.InputStream
import java.io.EOFException

typealias JInputStream = java.io.InputStream

open class JavaInputStream(private val stream: JInputStream) : InputStream {
    override fun read(): Byte {
        try {
            return stream.read().toByte()
        } catch (e: EOFException) {
            throw EndOfStreamException()
        }
    }

    override fun read(bytes: ByteArray): Int {
        try {
            return stream.read(bytes)
        } catch (e: EOFException) {
            throw EndOfStreamException()
        }
    }

    override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
        try {
            return stream.read(bytes, offset, length)
        } catch (e: EOFException) {
            throw EndOfStreamException()
        }
    }

    override fun readNBytes(n: Int): ByteArray {
        // NOTICE: This used to call stream.readNBytes but that is not available in the Android API we're compiling for.
        val bytes = ByteArray(n)
        return read(bytes, 0, n).let { if (it == -1) ByteArray(0) else bytes }
    }

    override fun skip(n: Long) = stream.skip(n)
}
