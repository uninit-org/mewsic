package net.sourceforge.jaad.aac.tools
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.aac.huffman.HCB
import net.sourceforge.jaad.aac.syntax.CPE
import net.sourceforge.jaad.aac.syntax.Constants

/**
 * Mid/side stereo
 * @author in-somnia
 */
object MS : Constants, HCB {
    fun process(cpe: CPE, specL: FloatArray, specR: FloatArray) {
        val ics = cpe.leftChannel
        val info = ics.info
        val offsets = info.sWBOffsets
        val windowGroups = info.windowGroupCount
        val maxSFB = info.maxSFB
        val sfbCBl = ics.sfbCB
        val sfbCBr = cpe.rightChannel.sfbCB
        var groupOff = 0
        var g: Int
        var i: Int
        var w: Int
        var j: Int
        var idx = 0
        g = 0
        while (g < windowGroups) {
            i = 0
            while (i < maxSFB) {
                if (cpe.isMSUsed(idx) && sfbCBl[idx] < HCB.NOISE_HCB && sfbCBr[idx] < HCB.NOISE_HCB) {
                    w = 0
                    while (w < info.getWindowGroupLength(g)) {
                        val off = groupOff + w * 128 + offsets[i]
                        j = 0
                        while (j < offsets[i + 1] - offsets[i]) {
                            val t = specL[off + j] - specR[off + j]
                            specL[off + j] += specR[off + j]
                            specR[off + j] = t
                            j++
                        }
                        w++
                    }
                }
                i++
                idx++
            }
            groupOff += info.getWindowGroupLength(g) * 128
            g++
        }
    }
}
