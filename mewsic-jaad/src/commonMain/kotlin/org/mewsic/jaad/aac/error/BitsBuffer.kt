package org.mewsic.jaad.aac.error

import org.mewsic.jaad.aac.AACException
import org.mewsic.jaad.aac.syntax.BitStream

class BitsBuffer {
    var bufa = 0
    var bufb = 0
    var length = 0

    fun showBits(bits: Int): Int {
        if (bits == 0) return 0
        return if (length <= 32) {
            //huffman_spectral_data_2 needs to read more than may be available,
            //bits maybe > len, deliver 0 than
            if (length >= bits) bufa shr length - bits and (-0x1 shr 32 - bits) else bufa shl bits - length and (-0x1 shr 32 - bits)
        } else {
            if (length - bits < 32) bufb and (-0x1 shr 64 - length) shl bits - length + 32 or (bufa shr length - bits) else bufb shr length - bits - 32 and (-0x1 shr 32 - bits)
        }
    }

    fun flushBits(bits: Int): Boolean {
        length -= bits
        val b: Boolean
        if (length < 0) {
            length = 0
            b = false
        } else b = true
        return b
    }

    fun getBits(n: Int): Int {
        var i = showBits(n)
        if (!flushBits(n)) i = -1
        return i
    }

    val bit: Int
        get() {
            var i = showBits(1)
            if (!flushBits(1)) i = -1
            return i
        }

    fun rewindReverse() {
        if (length == 0) return
        val i: IntArray = HCR.rewindReverse64(bufb, bufa, length)
        bufb = i[0]
        bufa = i[1]
    }

    //merge bits of a to b
    fun concatBits(a: BitsBuffer) {
        if (a.length == 0) return
        var al = a.bufa
        var ah = a.bufb
        val bl: Int
        val bh: Int
        if (length > 32) {
            //mask off superfluous high b bits
            bl = bufa
            bh = bufb and (1 shl length - 32) - 1
            //left shift a len bits
            ah = al shl length - 32
            al = 0
        } else {
            bl = bufa and (1 shl length) - 1
            bh = 0
            ah = ah shl length or (al shr 32 - length)
            al = al shl length
        }

        //merge
        bufa = bl or al
        bufb = bh or ah
        length += a.length
    }

    @Throws(AACException::class)
    fun readSegment(segwidth: Int, `in`: BitStream) {
        length = segwidth
        if (segwidth > 32) {
            bufb = `in`.readBits(segwidth - 32)
            bufa = `in`.readBits(32)
        } else {
            bufa = `in`.readBits(segwidth)
            bufb = 0
        }
    }
}
