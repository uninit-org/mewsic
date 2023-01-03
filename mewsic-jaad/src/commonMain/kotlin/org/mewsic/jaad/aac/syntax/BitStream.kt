package org.mewsic.jaad.aac.syntax

import org.mewsic.jaad.aac.AACException

class BitStream {
    private var buffer: ByteArray? = null
    private var pos = 0 //offset in the buffer array
    private var cache = 0 //current 4 bytes, that are read from the buffer
    protected var bitsCached = 0 //remaining bits in current cache
    var position = 0 //number of total bits read
        protected set

    constructor()
    constructor(data: ByteArray) {
        setData(data)
    }

    fun destroy() {
        reset()
        buffer = null
    }

    fun setData(data: ByteArray) {
        //make the buffer size an integer number of words
        val size = WORD_BYTES * ((data.size + WORD_BYTES - 1) / WORD_BYTES)
        //only reallocate if needed
        if (buffer == null || buffer!!.size != size) buffer = ByteArray(size)
        data.copyInto(buffer!!, 0, 0, data.size)
        reset()
    }

    @Throws(AACException::class)
    fun byteAlign() {
        val toFlush = bitsCached and 7
        if (toFlush > 0) skipBits(toFlush)
    }

    fun reset() {
        pos = 0
        bitsCached = 0
        cache = 0
        position = 0
    }

    val bitsLeft: Int
        get() = 8 * (buffer!!.size - pos) + bitsCached

    /**
     * Reads the next four bytes.
     * @param peek if true, the stream pointer will not be increased
     */
    @Throws(AACException::class)
    protected fun readCache(peek: Boolean): Int {
        val i: Int
        i = if (pos > buffer!!.size - WORD_BYTES) throw AACException(
            "end of stream",
            true
        ) else (buffer!![pos].toInt() and BYTE_MASK shl 24
                or (buffer!![pos + 1].toInt() and BYTE_MASK shl 16)
                or (buffer!![pos + 2].toInt() and BYTE_MASK shl 8)
                or (buffer!![pos + 3].toInt() and BYTE_MASK))
        if (!peek) pos += WORD_BYTES
        return i
    }

    @Throws(AACException::class)
    fun readBits(n: Int): Int {
        val result: Int
        if (bitsCached >= n) {
            bitsCached -= n
            result = cache shr bitsCached and maskBits(n)
            position += n
        } else {
            position += n
            val c = cache and maskBits(bitsCached)
            val left = n - bitsCached
            cache = readCache(false)
            bitsCached = WORD_BITS - left
            result = cache shr bitsCached and maskBits(left) or (c shl left)
        }
        return result
    }

    @Throws(AACException::class)
    fun readBit(): Int {
        val i: Int
        if (bitsCached > 0) {
            bitsCached--
            i = cache shr bitsCached and 1
            position++
        } else {
            cache = readCache(false)
            bitsCached = WORD_BITS - 1
            position++
            i = cache shr bitsCached and 1
        }
        return i
    }

    @Throws(AACException::class)
    fun readBool(): Boolean {
        return readBit() and 0x1 != 0
    }

    @Throws(AACException::class)
    fun peekBits(n: Int): Int {
        var n = n
        val ret: Int
        if (bitsCached >= n) {
            ret = cache shr bitsCached - n and maskBits(n)
        } else {
            //old cache
            val c = cache and maskBits(bitsCached)
            n -= bitsCached
            //read next & combine
            ret = readCache(true) shr WORD_BITS - n and maskBits(n) or (c shl n)
        }
        return ret
    }

    @Throws(AACException::class)
    fun peekBit(): Int {
        val ret: Int
        ret = if (bitsCached > 0) {
            cache shr bitsCached - 1 and 1
        } else {
            val word = readCache(true)
            word shr WORD_BITS - 1 and 1
        }
        return ret
    }

    @Throws(AACException::class)
    fun skipBits(n: Int) {
        var n = n
        position += n
        if (n <= bitsCached) {
            bitsCached -= n
        } else {
            n -= bitsCached
            while (n >= WORD_BITS) {
                n -= WORD_BITS
                readCache(false)
            }
            if (n > 0) {
                cache = readCache(false)
                bitsCached = WORD_BITS - n
            } else {
                cache = 0
                bitsCached = 0
            }
        }
    }

    @Throws(AACException::class)
    fun skipBit() {
        position++
        if (bitsCached > 0) {
            bitsCached--
        } else {
            cache = readCache(false)
            bitsCached = WORD_BITS - 1
        }
    }

    fun maskBits(n: Int): Int {
        val i: Int
        i = if (n == 32) -1 else (1 shl n) - 1
        return i
    }

    companion object {
        private const val WORD_BITS = 32
        private const val WORD_BYTES = 4
        private const val BYTE_MASK = 0xff
    }
}
