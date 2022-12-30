package net.sourceforge.jaad.mp4

import org.mewsic.commons.lang.Arrays
import org.mewsic.commons.streams.FileInputStream
import org.mewsic.commons.streams.api.InputStream
import kotlin.math.*

// TODO: This needs to be somewhat completely rewritten
//  to more conform to the multiplatform mewsic codebase.
class MP4InputStream {
    private val `in`: InputStream?
    private val fin: FileInputStream?
    private var peeked: MutableList<Byte> = mutableListOf()
    private var offset: Long = 0 //only used with InputStream

    /**
     * Constructs an `MP4InputStream` that reads from an
     * `InputStream`. It will have no random access, thus seeking
     * will not be possible.
     *
     * @param in an `InputStream` to read from
     */
    internal constructor(`in`: InputStream) {
        this.`in` = `in`
        fin = null
        offset = 0
    }

    /**
     * Constructs an `MP4InputStream` that reads from a
     * `RandomAccessFile`. It will have random access and seeking
     * will be possible.
     *
     * @param in a `RandomAccessFile` to read from
     */
    internal constructor(fin: FileInputStream?) {
        this.fin = fin
        `in` = null
    }

    /**
     * Peeks the next byte of data from the input. The value byte is returned as
     * an int in the range 0 to 255. If no byte is available because the end of
     * the stream has been reached, an EOFException is thrown. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an I/O error occurs.
     *
     * @return the next byte of data
     * @throws IOException If the end of the stream is detected or any I/O error occurs.
     */
    @Throws(Exception::class)
    fun peek(): Int {
        var i = 0
        if (!peeked.isEmpty()) {
            i = peeked.removeFirst().toInt() and MASK8
        } else if (`in` != null) {
            i = `in`.read().toInt()
        } else if (fin != null) {
            val currentFilePointer: Long = fin.position()
            i = try {
                fin.read().toInt()
            } finally {
                fin.seek(currentFilePointer)
            }
        }
        if (i == -1) {
            throw Exception("EOF")
        }
        peeked = (listOf(i.toByte()) + peeked).toMutableList()
        return i
    }

    /**
     * Reads the next byte of data from the input. The value byte is returned as
     * an int in the range 0 to 255. If no byte is available because the end of
     * the stream has been reached, an EOFException is thrown. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an I/O error occurs.
     *
     * @return the next byte of data
     * @throws IOException If the end of the stream is detected or any I/O error occurs.
     */
    @Throws(Exception::class)
    fun read(): Int {
        var i = 0
        if (!peeked.isEmpty()) {
            i = peeked.removeFirst().toInt() and MASK8
        } else if (`in` != null) {
            i = `in`.read().toInt()
        } else if (fin != null) {
            i = fin.read().toInt()
        }
        if (i == -1) {
            throw Exception("EOF")
        } else if (`in` != null) {
            offset++
        }
        return i
    }

    /**
     * Peeks `len` bytes of data from the input into the array
     * `b`. If len is zero, then no bytes are read.
     *
     * This method blocks until all bytes could be read, the end of the stream
     * is detected, or an I/O error occurs.
     *
     * If the stream ends before `len` bytes could be read an
     * EOFException is thrown.
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset in array `b` at which the data is written.
     * @param len the number of bytes to read.
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     */
    @Throws(Exception::class)
    fun peek(b: ByteArray, off: Int, len: Int) {
        var read = 0
        var i = 0
        while (read < len && read < peeked.size) {
            b[off + read] = peeked.get(read)
            read++
        }
        var currentFilePointer: Long = -1
        if (fin != null) {
            currentFilePointer = fin.position()
        }
        try {
            while (read < len) {
                if (`in` != null) {
                    i = `in`.read(b, off + read, len - read)
                } else if (fin != null) {
                    i = fin.read(b, off + read, len - read)
                }
                read += if (i < 0) {
                    throw Exception("EOF")
                } else {
                    for (j in 0 until i) {
                        peeked.add(b[off + j])
                    }
                    i
                }
            }
        } finally {
            fin?.seek(currentFilePointer)
        }
    }

    /**
     * Reads `len` bytes of data from the input into the array
     * `b`. If len is zero, then no bytes are read.
     *
     * This method blocks until all bytes could be read, the end of the stream
     * is detected, or an I/O error occurs.
     *
     * If the stream ends before `len` bytes could be read an
     * EOFException is thrown.
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset in array `b` at which the data is written.
     * @param len the number of bytes to read.
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     */
    @Throws(Exception::class)
    fun read(b: ByteArray, off: Int, len: Int) {
        var read = 0
        var i = 0
        while (read < len && !peeked.isEmpty()) {
            b[off + read] = peeked.removeFirst()
            read++
        }
        while (read < len) {
            if (`in` != null) i = `in`.read(b, off + read, len - read) else if (fin != null) i =
                fin.read(b, off + read, len - read)
            read += if (i < 0) throw Exception("EOF") else i
        }
        offset += read.toLong()
    }

    /**
     * Peeks up to eight bytes as a long value. This method blocks until all
     * bytes could be read, the end of the stream is detected, or an I/O error
     * occurs.
     *
     * @param n the number of bytes to read >0 and <=8
     * @return the read bytes as a long value
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     * @throws IndexOutOfBoundsException if `n` is not in the range
     * [1...8] inclusive.
     */
    @Throws(Exception::class)
    fun peekBytes(n: Int): Long {
        if (n < 1 || n > 8) throw IndexOutOfBoundsException("invalid number of bytes to read: $n")
        val b = ByteArray(n)
        peek(b, 0, n)
        var result: Long = 0
        for (i in 0 until n) {
            result = result shl 8 or (b[i].toInt() and 0xFF).toLong()
        }
        return result
    }

    /**
     * Reads up to eight bytes as a long value. This method blocks until all
     * bytes could be read, the end of the stream is detected, or an I/O error
     * occurs.
     *
     * @param n the number of bytes to read >0 and <=8
     * @return the read bytes as a long value
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     * @throws IndexOutOfBoundsException if `n` is not in the range
     * [1...8] inclusive.
     */
    @Throws(Exception::class)
    fun readBytes(n: Int): Long {
        if (n < 1 || n > 8) throw IndexOutOfBoundsException("invalid number of bytes to read: $n")
        val b = ByteArray(n)
        read(b, 0, n)
        var result: Long = 0
        for (i in 0 until n) {
            result = result shl 8 or (b[i].toInt() and 0xFF).toLong()
        }
        return result
    }

    /**
     * Peeks data from the input stream and stores them into the buffer array b.
     * This method blocks until all bytes could be read, the end of the stream
     * is detected, or an I/O error occurs.
     * If the length of b is zero, then no bytes are read.
     *
     * @param b the buffer into which the data is read.
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     */
    @Throws(Exception::class)
    fun peekBytes(b: ByteArray) {
        peek(b, 0, b.size)
    }

    /**
     * Reads data from the input stream and stores them into the buffer array b.
     * This method blocks until all bytes could be read, the end of the stream
     * is detected, or an I/O error occurs.
     * If the length of b is zero, then no bytes are read.
     *
     * @param b the buffer into which the data is read.
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     */
    @Throws(Exception::class)
    fun readBytes(b: ByteArray) {
        read(b, 0, b.size)
    }

    /**
     * Reads `n` bytes from the input as a String. The bytes are
     * directly converted into characters. If not enough bytes could be read, an
     * EOFException is thrown.
     * This method blocks until all bytes could be read, the end of the stream
     * is detected, or an I/O error occurs.
     *
     * @param n the length of the String.
     * @return the String, that was read
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     */
    @Throws(Exception::class)
    fun readString(n: Int): String {
        var i = -1
        var pos = 0
        val c = CharArray(n)
        while (pos < n) {
            i = read()
            c[pos] = i.toChar()
            pos++
        }
        return c.contentToString()
    }

    /**
     * Reads a null-terminated UTF-encoded String from the input. The maximum
     * number of bytes that can be read before the null must appear must be
     * specified.
     * Although the method is preferred for unicode, the encoding can be any
     * charset name, that is supported by the system.
     *
     * This method blocks until all bytes could be read, the end of the stream
     * is detected, or an I/O error occurs.
     *
     * @param max the maximum number of bytes to read, before the null-terminator
     * must appear.
     * @param encoding the charset used to encode the String
     * @return the decoded String
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     */
    @Throws(Exception::class)
    fun readUTFString(max: Int, encoding: String?): String {
        return readTerminated(max, 0).decodeToString() // FIXME: encoding
    }

    /**
     * Reads a null-terminated UTF-encoded String from the input. The maximum
     * number of bytes that can be read before the null must appear must be
     * specified.
     * The encoding is detected automatically, it may be UTF-8 or UTF-16
     * (determined by a byte order mask at the beginning).
     *
     * This method blocks until all bytes could be read, the end of the stream
     * is detected, or an I/O error occurs.
     *
     * @param max the maximum number of bytes to read, before the null-terminator
     * must appear.
     * @return the decoded String
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     */
    @Throws(Exception::class)
    fun readUTFString(max: Int): String {
        //read byte order mask
        val bom = ByteArray(2)
        read(bom, 0, 2)
        if (bom[0].toInt() == 0 || bom[1].toInt() == 0) return String()
        val i = bom[0].toInt() shl 8 or bom[1].toInt()

        //read null-terminated
        val b = readTerminated(max - 2, 0)
        //copy bom
        val b2 = ByteArray(b.size + bom.size)
        Arrays.arraycopy(bom, 0, b2, 0, bom.size)
        Arrays.arraycopy(b, 0, b2, bom.size, b.size)
        // String(b2, java.nio.charset.Charset.forName(if (i == BYTE_ORDER_MASK) UTF16 else UTF8))
        // FIXME: encoding
        return b2.decodeToString()
    }

    /**
     * Reads a byte array from the input that is terminated by a specific byte
     * (the 'terminator'). The maximum number of bytes that can be read before
     * the terminator must appear must be specified.
     *
     * The terminator will not be included in the returned array.
     *
     * This method blocks until all bytes could be read, the end of the stream
     * is detected, or an I/O error occurs.
     *
     * @param max the maximum number of bytes to read, before the terminator
     * must appear.
     * @param terminator the byte that indicates the end of the array
     * @return the buffer into which the data is read.
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     */
    @Throws(Exception::class)
    fun readTerminated(max: Int, terminator: Int): ByteArray {
        val b = ByteArray(max)
        var pos = 0
        var i = 0
        while (pos < max && i != -1) {
            i = read()
            if (i != -1) b[pos++] = i.toByte()
        }
        return Arrays.copyOf(b, pos)
    }

    /**
     * Reads a fixed point number from the input. The number is read as a
     * `m.n` value, that results from deviding an integer by
     * 2<sup>n</sup>.
     *
     * @param m the number of bits before the point
     * @param n the number of bits after the point
     * @return a floating point number with the same value
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     * @throws IllegalArgumentException if the total number of bits (m+n) is not
     * a multiple of eight
     */
    @Throws(Exception::class)
    fun readFixedPoint(m: Int, n: Int): Double {
        val bits = m + n
        if (bits % 8 != 0) throw IllegalArgumentException("number of bits is not a multiple of 8: " + (m + n))
        val l = readBytes(bits / 8)
        val x: Double = 2.0.pow(n.toDouble())
        return l.toDouble() / x
    }

    /**
     * Skips `n` bytes in the input. This method blocks until all
     * bytes could be skipped, the end of the stream is detected, or an I/O
     * error occurs.
     *
     * @param n the number of bytes to skip
     * @throws IOException If the end of the stream is detected, the input
     * stream has been closed, or if some other I/O error occurs.
     */
    @Throws(Exception::class)
    fun skipBytes(n: Long) {
        var l: Long = 0
        while (l < n && !peeked.isEmpty()) {
            peeked.removeFirst()
            l++
        }
        while (l < n) {
            if (`in` != null) l += `in`.skip(n - l) else if (fin != null) l += fin.skip((n - l)).toLong()
        }
        offset += l
    }

    /**
     * Returns the current offset in the stream.
     *
     * @return the current offset
     * @throws IOException if an I/O error occurs (only when using a RandomAccessFile)
     */
    @Throws(Exception::class)
    fun getOffset(): Long {
        var l: Long = -1
        if (`in` != null) l = offset else if (fin != null) l = fin.position()
        return l
    }

    /**
     * Seeks to a specific offset in the stream. This is only possible when
     * using a RandomAccessFile. If an InputStream is used, this method throws
     * an IOException.
     *
     * @param pos the offset position, measured in bytes from the beginning of the
     * stream
     * @throws IOException if an InputStream is used, pos is less than 0 or an
     * I/O error occurs
     */
    @Throws(Exception::class)
    fun seek(pos: Long) {
        peeked.clear()
        if (fin != null) fin.seek(pos) else throw Exception("could not seek: no random access")
    }

    /**
     * Indicates, if random access is available. That is, if this
     * `MP4InputStream` was constructed with a RandomAccessFile. If
     * this method returns false, seeking is not possible.
     *
     * @return true if random access is available
     */
    fun hasRandomAccess(): Boolean {
        return fin != null
    }

    /**
     * Indicates, if the input has some data left.
     *
     * @return true if there is at least one byte left
     * @throws IOException if an I/O error occurs
     */
    @Throws(Exception::class)
    fun hasLeft(): Boolean {
        val b: Boolean
        if (!peeked.isEmpty()) {
            b = true
        } else if (fin != null) {
            b = fin.position() < fin.length() - 1
        } else if (`in` != null) {
            val i: Int = `in`.read().toInt()
            b = i != -1
            if (b) peeked.add(i.toByte())
        } else {
            b = false
        }
        return b
    }

    /**
     * Closes the input and releases any system resources associated with it.
     * Once the stream has been closed, further reading or skipping will throw
     * an IOException. Closing a previously closed stream has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Throws(Exception::class)
    fun close() {
        peeked.clear()
//        if (`in` != null) `in`.gc() else if (fin != null) fin.close()

    }

    companion object {
        const val MASK8 = 0xFF
        const val MASK16 = 0xFFFF
        const val UTF8 = "UTF-8"
        const val UTF16 = "UTF-16"
        private const val BYTE_ORDER_MASK = 0xFEFF
    }
}
