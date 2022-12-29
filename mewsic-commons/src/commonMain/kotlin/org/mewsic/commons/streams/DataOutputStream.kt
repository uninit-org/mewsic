package org.mewsic.commons.streams

import org.mewsic.commons.streams.api.OutputStream

class DataOutputStream(private val stream: OutputStream, private val littleEndian: Boolean = true) : OutputStream by stream {
    fun writeEndian(n: Long, size: Int) {
        var value = n
        val bytes = ByteArray(size)
        for (i in 0 until size) {
            bytes[i] = (value and 0xFF).toByte()
            value = value shr 8
        }
        if (!littleEndian) {
            bytes.reverse()
        }
        stream.write(bytes)
    }

    fun writeByte(value: Byte) {
        stream.write(value)
    }

    fun writeUByte(value: UByte) {
        stream.write(value.toByte())
    }

    fun writeShort(value: Short) {
        writeEndian(value.toLong(), 2)
    }

    fun writeUShort(value: UShort) {
        writeEndian(value.toLong(), 2)
    }

    fun writeInt(value: Int) {
        writeEndian(value.toLong(), 4)
    }

    fun writeUInt(value: UInt) {
        writeEndian(value.toLong(), 4)
    }

    fun writeLong(value: Long) {
        writeEndian(value, 8)
    }

    fun writeULong(value: ULong) {
        writeEndian(value.toLong(), 8)
    }

    fun writeFloat(value: Float) {
        writeInt(value.toRawBits())
    }

    fun writeDouble(value: Double) {
        writeLong(value.toRawBits())
    }

    fun writeString(value: String) {
        val bytes = value.encodeToByteArray()
        stream.write(bytes)
    }

    fun writeStringNullTerminated(value: String) {
        val bytes = value.encodeToByteArray()
        stream.write(bytes)
        stream.write(0)
    }
}
