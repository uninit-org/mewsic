package org.mewsic.commons.binary

import org.mewsic.commons.streams.api.OutputStream

open class BinaryWriter(protected val stream: OutputStream, private val littleEndian: Boolean = true) {
    fun write(arr: ByteArray) {
        stream.write(arr)
    }

    fun writeEndian(size: Int, value: Long) {
        val arr = ByteArray(size)
        for (i in 0 until size) {
            arr[i] = (value shr (i * 8)).toByte()
        }
        if (littleEndian) {
            arr.reverse()
        }
        stream.write(arr)
    }

    fun writeByte(value: Byte) = stream.write(value)
    fun writeUByte(value: UByte) = stream.write(value.toByte())
    fun writeShort(value: Short) = writeEndian(2, value.toLong())
    fun writeUShort(value: UShort) = writeEndian(2, value.toLong())
    fun writeInt(value: Int) = writeEndian(4, value.toLong())
    fun writeUInt(value: UInt) = writeEndian(4, value.toLong())
    fun writeLong(value: Long) = writeEndian(8, value)
    fun writeULong(value: ULong) = writeEndian(8, value.toLong())
    fun writeFloat(value: Float) = writeInt(value.toRawBits())
    fun writeDouble(value: Double) = writeLong(value.toRawBits())
    fun writeString(value: String) = stream.write(value.encodeToByteArray())
    fun writeStringNullTerminated(value: String) {
        stream.write(value.encodeToByteArray())
        stream.write(0.toByte())
    }

    fun skip(n: Long) = stream.skip(n)
}
