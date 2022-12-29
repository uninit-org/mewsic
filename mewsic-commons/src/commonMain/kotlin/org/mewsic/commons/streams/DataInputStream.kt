package org.mewsic.commons.streams

import org.mewsic.commons.streams.api.InputStream

class DataInputStream(private val stream: InputStream, private val littleEndian: Boolean = true) : InputStream by stream {
    fun readEndian(n: Int): Long {
        var result = 0L
        val bytes = stream.readNBytes(n)
        if (littleEndian) {
            bytes.reverse()
        }
        for (i in 0 until n) {
            result = result or ((bytes[i].toLong() and 0xFF) shl (8 * (n - i - 1)))
        }
        return result
    }

    fun readByte(): Byte {
        return stream.read()
    }

    fun readUByte(): UByte {
        return stream.read().toUByte()
    }

    fun readShort(): Short {
        return readEndian(2).toShort()
    }

    fun readUShort(): UShort {
        return readEndian(2).toUShort()
    }

    fun readInt(): Int {
        return readEndian(4).toInt()
    }

    fun readUInt(): UInt {
        return readEndian(4).toUInt()
    }

    fun readLong(): Long {
        return readEndian(8)
    }

    fun readULong(): ULong {
        return readEndian(8).toULong()
    }

    fun readFloat(): Float {
        return Float.fromBits(readInt())
    }

    fun readDouble(): Double {
        return Double.fromBits(readLong())
    }

    fun readString(length: Int): String {
        val bytes = stream.readNBytes(length)
        return bytes.decodeToString()
    }

    fun readStringNullTerminated(): String {
        val bytes = mutableListOf<Byte>()
        while (true) {
            val b = stream.read()
            if (b == 0.toByte()) {
                break
            }
            bytes.add(b)
        }
        return bytes.toByteArray().decodeToString()
    }
}
