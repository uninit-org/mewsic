package org.mewsic.jaad.aac.huffman

import org.mewsic.jaad.aac.AACException
import org.mewsic.jaad.aac.huffman.Codebooks.Companion.CODEBOOKS
import org.mewsic.jaad.aac.huffman.Codebooks.Companion.HCB_SF
import org.mewsic.jaad.aac.syntax.BitStream
import kotlin.math.abs

//TODO: implement decodeSpectralDataER
object Huffman : Codebooks {
    private val UNSIGNED = booleanArrayOf(false, false, true, true, false, false, true, true, true, true, true)
    private const val QUAD_LEN = 4
    private const val PAIR_LEN = 2

    @Throws(AACException::class)
    private fun findOffset(`in`: BitStream, table: Array<IntArray>): Int {
        var off = 0
        var len = table[off][0]
        var cw = `in`.readBits(len)
        var j: Int
        while (cw != table[off][1]) {
            off++
            j = table[off][0] - len
            len = table[off][0]
            cw = cw shl j
            cw = cw or `in`.readBits(j)
        }
        return off
    }

    @Throws(AACException::class)
    private fun signValues(`in`: BitStream, data: IntArray, off: Int, len: Int) {
        for (i in off until off + len) {
            if (data[i] != 0) {
                if (`in`.readBool()) data[i] = -data[i]
            }
        }
    }

    @Throws(AACException::class)
    private fun getEscape(`in`: BitStream, s: Int): Int {
        val neg = s < 0
        var i = 4
        while (`in`.readBool()) {
            i++
        }
        val j = `in`.readBits(i) or (1 shl i)
        return if (neg) -j else j
    }

    @Throws(AACException::class)
    fun decodeScaleFactor(`in`: BitStream): Int {
        val offset = findOffset(`in`, HCB_SF)
        return HCB_SF.get(offset).get(2)
    }

    @Throws(AACException::class)
    fun decodeSpectralData(`in`: BitStream, cb: Int, data: IntArray, off: Int) {
        val HCB: Array<IntArray> = CODEBOOKS.get(cb - 1)

        //find index
        val offset = findOffset(`in`, HCB)

        //copy data
        data[off] = HCB[offset][2]
        data[off + 1] = HCB[offset][3]
        if (cb < 5) {
            data[off + 2] = HCB[offset][4]
            data[off + 3] = HCB[offset][5]
        }

        //sign & escape
        if (cb < 11) {
            if (UNSIGNED[cb - 1]) signValues(`in`, data, off, if (cb < 5) QUAD_LEN else PAIR_LEN)
        } else if (cb == 11 || cb > 15) {
            signValues(`in`, data, off, if (cb < 5) QUAD_LEN else PAIR_LEN) //virtual codebooks are always unsigned
            if (abs(data[off]) == 16) data[off] = getEscape(`in`, data[off])
            if (abs(data[off + 1]) == 16) data[off + 1] = getEscape(`in`, data[off + 1])
        } else throw AACException("Huffman: unknown spectral codebook: $cb")
    }
}
