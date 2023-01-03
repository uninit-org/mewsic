package org.mewsic.jaad.aac.tools

import org.mewsic.jaad.aac.huffman.HCB
import org.mewsic.jaad.aac.syntax.CPE
import org.mewsic.jaad.aac.syntax.Constants

/**
 * Intensity stereo
 * @author in-somnia
 */
object IS : Constants, ISScaleTable, HCB {
    fun process(cpe: CPE, specL: FloatArray, specR: FloatArray) {
        val ics = cpe.rightChannel
        val info = ics.info
        val offsets = info.sWBOffsets
        val windowGroups = info.windowGroupCount
        val maxSFB = info.maxSFB
        val sfbCB = ics.sfbCB
        val sectEnd = ics.sectEnd
        val scaleFactors = ics.scaleFactors
        var w: Int
        var i: Int
        var j: Int
        var c: Int
        var end: Int
        var off: Int
        var idx = 0
        var groupOff = 0
        var scale: Float
        for (g in 0 until windowGroups) {
            i = 0
            while (i < maxSFB) {
                if (sfbCB[idx] == HCB.INTENSITY_HCB || sfbCB[idx] == HCB.INTENSITY_HCB2) {
                    end = sectEnd[idx]
                    while (i < end) {
                        c = if (sfbCB[idx] == HCB.INTENSITY_HCB) 1 else -1
                        if (cpe.isMSMaskPresent) c *= if (cpe.isMSUsed(idx)) -1 else 1
                        scale = c * scaleFactors[idx]
                        w = 0
                        while (w < info.getWindowGroupLength(g)) {
                            off = groupOff + w * 128 + offsets[i]
                            j = 0
                            while (j < offsets[i + 1] - offsets[i]) {
                                specR[off + j] = specL[off + j] * scale
                                j++
                            }
                            w++
                        }
                        i++
                        idx++
                    }
                } else {
                    end = sectEnd[idx]
                    idx += end - i
                    i = end
                }
            }
            groupOff += info.getWindowGroupLength(g) * 128
        }
    }
}
