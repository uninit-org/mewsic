package net.sourceforge.jaad.aac.error
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.huffman.HCB
import net.sourceforge.jaad.aac.syntax.BitStream
import net.sourceforge.jaad.aac.syntax.ICSInfo
import net.sourceforge.jaad.aac.syntax.ICStream

/**
 * Reversable variable length coding
 * Decodes scalefactors if error resilience is used.
 */
class RVLC : RVLCTables {
    @Throws(AACException::class)
    override fun decode(`in`: BitStream, ics: ICStream, scaleFactors: Array<IntArray>) {
        val bits = if (ics.info.isEightShortFrame) 11 else 9
        val sfConcealment = `in`.readBool()
        val revGlobalGain = `in`.readBits(8)
        val rvlcSFLen = `in`.readBits(bits)
        val info: ICSInfo = ics.info
        val windowGroupCount: Int = info.windowGroupCount
        val maxSFB: Int = info.maxSFB
        val sfbCB: Array<IntArray>? = null //ics.getSectionData().getSfbCB();
        var sf: Int = ics.globalGain
        var intensityPosition = 0
        var noiseEnergy = sf - 90 - 256
        var intensityUsed = false
        var noiseUsed = false
        var sfb: Int
        for (g in 0 until windowGroupCount) {
            sfb = 0
            while (sfb < maxSFB) {
                when (sfbCB!![g][sfb]) {
                    HCB.ZERO_HCB -> scaleFactors[g][sfb] = 0
                    HCB.INTENSITY_HCB, HCB.INTENSITY_HCB2 -> {
                        if (!intensityUsed) intensityUsed = true
                        intensityPosition += decodeHuffman(`in`)
                        scaleFactors[g][sfb] = intensityPosition
                    }

                    HCB.NOISE_HCB -> if (noiseUsed) {
                        noiseEnergy += decodeHuffman(`in`)
                        scaleFactors[g][sfb] = noiseEnergy
                    } else {
                        noiseUsed = true
                        noiseEnergy = decodeHuffman(`in`)
                    }

                    else -> {
                        sf += decodeHuffman(`in`)
                        scaleFactors[g][sfb] = sf
                    }
                }
                sfb++
            }
        }
        var lastIntensityPosition = 0
        if (intensityUsed) lastIntensityPosition = decodeHuffman(`in`)
        noiseUsed = false
        if (`in`.readBool()) decodeEscapes(`in`, ics, scaleFactors)
    }

    @Throws(AACException::class)
    private fun decodeEscapes(`in`: BitStream, ics: ICStream, scaleFactors: Array<IntArray>) {
        val info: ICSInfo = ics.getInfo()
        val windowGroupCount: Int = info.windowGroupCount
        val maxSFB: Int = info.maxSFB
        val sfbCB: Array<IntArray>? = null //ics.getSectionData().getSfbCB();
        val escapesLen = `in`.readBits(8)
        var noiseUsed = false
        var sfb: Int
        var `val`: Int
        for (g in 0 until windowGroupCount) {
            sfb = 0
            while (sfb < maxSFB) {
                if (sfbCB!![g][sfb] == HCB.NOISE_HCB && !noiseUsed) noiseUsed = true else if (java.lang.Math.abs(
                        sfbCB[g][sfb]
                    ) == ESCAPE_FLAG
                ) {
                    `val` = decodeHuffmanEscape(`in`)
                    if (sfbCB[g][sfb] == -ESCAPE_FLAG) scaleFactors[g][sfb] -= `val` else scaleFactors[g][sfb] += `val`
                }
                sfb++
            }
        }
    }

    @Throws(AACException::class)
    private fun decodeHuffman(`in`: BitStream): Int {
        var off = 0
        var i: Int = RVLC_BOOK.get(off).get(1)
        var cw = `in`.readBits(i)
        var j: Int
        while (cw != RVLC_BOOK.get(off).get(2) && i < 10) {
            off++
            j = RVLC_BOOK.get(off).get(1) - i
            i += j
            cw = cw shl j
            cw = cw or `in`.readBits(j)
        }
        return RVLC_BOOK.get(off).get(0)
    }

    @Throws(AACException::class)
    private fun decodeHuffmanEscape(`in`: BitStream): Int {
        var off = 0
        var i: Int = ESCAPE_BOOK.get(off).get(1)
        var cw = `in`.readBits(i)
        var j: Int
        while (cw != ESCAPE_BOOK.get(off).get(2) && i < 21) {
            off++
            j = ESCAPE_BOOK.get(off).get(1) - i
            i += j
            cw = cw shl j
            cw = cw or `in`.readBits(j)
        }
        return ESCAPE_BOOK.get(off).get(0)
    }

    companion object {
        private const val ESCAPE_FLAG = 7
    }
}
