package org.mewsic.commons.binary

import org.mewsic.commons.streams.api.InputStream

open class BinaryReader(protected val stream: InputStream, private val bigEndian: Boolean = true) {
    fun read(n: Int): ByteArray {
        val bytes = ByteArray(n)
        stream.read(bytes)
        return bytes
    }

    fun readEndian(n: Int): Long {
        val bytes = ByteArray(n)
        stream.read(bytes)
        if (bigEndian) {
            bytes.reverse()
        }
        var value = 0L
        for (i in 0 until n) {
            value = value or ((bytes[i].toLong() and 0xFF) shl (i * 8))
        }
        return value
    }

    fun readByte(): Byte = stream.read()
    fun readUByte(): UByte = stream.read().toUByte()
    fun readShort(): Short = readEndian(2).toShort()
    fun readUShort(): UShort = readEndian(2).toUShort()
    fun readInt(): Int = readEndian(4).toInt()
    fun readUInt(): UInt = readEndian(4).toUInt()
    fun readLong(): Long = readEndian(8)
    fun readULong(): ULong = readEndian(8).toULong()
    fun readFloat(): Float = Float.fromBits(readInt())
    fun readDouble(): Double = Double.fromBits(readLong())
    fun readString(length: Int): String {
        val bytes = ByteArray(length)
        stream.read(bytes)
        return bytes.decodeToString()
    }
    fun readStringNullTerminated(): String {
        val bytes = mutableListOf<Byte>()
        while (true) {
            val byte = stream.read()
            if (byte == 0.toByte()) {
                break
            }
            bytes.add(byte)
        }
        return bytes.toByteArray().decodeToString()
    }

    fun skip(n: Long) = stream.skip(n)
}
