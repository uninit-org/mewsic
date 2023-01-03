package org.mewsic.jaad.aac.tools

import org.mewsic.jaad.aac.AACException
import org.mewsic.jaad.aac.SampleFrequency
import org.mewsic.jaad.aac.syntax.*
import org.mewsic.jaad.aac.tools.TNSTables.Companion.TNS_TABLES

/**
 * Temporal Noise Shaping
 * @author in-somnia
 */
class TNS : Constants, TNSTables {
    //bitstream
    private val nFilt: IntArray
    private val length: Array<IntArray>
    private val order: Array<IntArray>
    private val direction: Array<BooleanArray>
    private val coef: Array<Array<FloatArray>>

    init {
        nFilt = IntArray(8)
        length = Array(8) { IntArray(4) }
        direction = Array(8) { BooleanArray(4) }
        order = Array(8) { IntArray(4) }
        coef = Array(8) { Array(4) { FloatArray(TNS_MAX_ORDER) } }
    }

    @Throws(AACException::class)
    fun decode(`in`: BitStream, info: ICSInfo) {
        val windowCount = info.windowCount
        val bits = if (info.isEightShortFrame) SHORT_BITS else LONG_BITS
        var w: Int
        var i: Int
        var filt: Int
        var coefLen: Int
        var coefRes: Int
        var coefCompress: Int
        var tmp: Int
        w = 0
        while (w < windowCount) {
            if (`in`.readBits(bits[0]).also { nFilt[w] = it } != 0) {
                coefRes = `in`.readBit()
                filt = 0
                while (filt < nFilt[w]) {
                    length[w][filt] = `in`.readBits(bits[1])
                    if (`in`.readBits(bits[2])
                            .also { order[w][filt] = it } > 20
                    ) throw AACException("TNS filter out of range: " + order[w][filt]) else if (order[w][filt] != 0) {
                        direction[w][filt] = `in`.readBool()
                        coefCompress = `in`.readBit()
                        coefLen = coefRes + 3 - coefCompress
                        tmp = 2 * coefCompress + coefRes
                        i = 0
                        while (i < order[w][filt]) {
                            coef[w][filt][i] = TNS_TABLES.get(tmp).get(`in`.readBits(coefLen))
                            i++
                        }
                    }
                    filt++
                }
            }
            w++
        }
    }

    fun process(ics: ICStream?, spec: FloatArray?, sf: SampleFrequency?, decode: Boolean) {
        //TODO...
    }

    companion object {
        private const val TNS_MAX_ORDER = 20
        private val SHORT_BITS = intArrayOf(1, 4, 3)
        private val LONG_BITS = intArrayOf(2, 6, 5)
    }
}
