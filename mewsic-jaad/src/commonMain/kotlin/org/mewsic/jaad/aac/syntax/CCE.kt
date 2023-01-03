package org.mewsic.jaad.aac.syntax

import org.mewsic.jaad.aac.AACException
import org.mewsic.jaad.aac.DecoderConfig
import org.mewsic.jaad.aac.huffman.HCB
import org.mewsic.jaad.aac.huffman.Huffman
import kotlin.math.pow

internal class CCE(frameLength: Int) : Element(),
    Constants {
    private val ics: ICStream
    private lateinit var iqData: FloatArray
    var couplingPoint = 0
        private set
    var coupledCount = 0
        private set
    private val channelPair: BooleanArray
    private val idSelect: IntArray
    private val chSelect: IntArray

    /*[0] shared list of gains; [1] list of gains for right channel;
	 *[2] list of gains for left channel; [3] lists of gains for both channels
	 */
    private val gain: Array<FloatArray>

    init {
        ics = ICStream(frameLength)
        channelPair = BooleanArray(8)
        idSelect = IntArray(8)
        chSelect = IntArray(8)
        gain = Array(16) { FloatArray(120) }
    }

    fun isChannelPair(index: Int): Boolean {
        return channelPair[index]
    }

    fun getIDSelect(index: Int): Int {
        return idSelect[index]
    }

    fun getCHSelect(index: Int): Int {
        return chSelect[index]
    }

    @Throws(AACException::class)
    fun decode(`in`: BitStream, conf: DecoderConfig?) {
        couplingPoint = 2 * `in`.readBit()
        coupledCount = `in`.readBits(3)
        var gainCount = 0
        var i: Int
        i = 0
        while (i <= coupledCount) {
            gainCount++
            channelPair[i] = `in`.readBool()
            idSelect[i] = `in`.readBits(4)
            if (channelPair[i]) {
                chSelect[i] = `in`.readBits(2)
                if (chSelect[i] == 3) gainCount++
            } else chSelect[i] = 2
            i++
        }
        couplingPoint += `in`.readBit()
        couplingPoint = couplingPoint or (couplingPoint shr 1)
        val sign = `in`.readBool()
        val scale = CCE_SCALE[`in`.readBits(2)].toDouble()
        ics.decode(`in`, false, conf!!)
        val info: ICSInfo = ics.info
        val windowGroupCount: Int = info.windowGroupCount
        val maxSFB: Int = info.maxSFB
        //TODO:
        val sfbCB: Array<IntArray>? = null //ics.getSectionData().getSfbCB();
        i = 0
        while (i < gainCount) {
            var idx = 0
            var cge = 1
            var xg = 0
            var gainCache = 1.0f
            if (i > 0) {
                cge = if (couplingPoint == 2) 1 else `in`.readBit()
                xg = if (cge == 0) 0 else Huffman.decodeScaleFactor(`in`) - 60
                gainCache = scale.pow(-xg.toDouble()).toFloat()
            }
            if (couplingPoint == 2) gain[i][0] = gainCache else {
                var sfb: Int
                for (g in 0 until windowGroupCount) {
                    sfb = 0
                    while (sfb < maxSFB) {
                        if (sfbCB!![g][sfb] != HCB.ZERO_HCB) {
                            if (cge == 0) {
                                var t: Int = Huffman.decodeScaleFactor(`in`) - 60
                                if (t != 0) {
                                    var s = 1
                                    xg += t
                                    t = xg
                                    if (!sign) {
                                        s -= 2 * (t and 0x1)
                                        t = t shr 1
                                    }
                                    gainCache = (scale.pow(-t.toDouble()) * s).toFloat()
                                }
                            }
                            gain[i][idx] = gainCache
                        }
                        sfb++
                        idx++
                    }
                }
            }
            i++
        }
    }

    @Throws(AACException::class)
    fun process() {
        iqData = ics.invQuantData
    }

    fun applyIndependentCoupling(index: Int, data: FloatArray) {
        val g = gain[index][0].toDouble()
        for (i in data.indices) {
            data[i] += (g * iqData[i]).toFloat()
        }
    }

    fun applyDependentCoupling(index: Int, data: FloatArray) {
        val info: ICSInfo = ics.info
        val swbOffsets: IntArray = info.sWBOffsets
        val windowGroupCount: Int = info.windowGroupCount
        val maxSFB: Int = info.maxSFB
        //TODO:
        val sfbCB: Array<IntArray>? = null //ics.getSectionData().getSfbCB();
        var srcOff = 0
        var dstOff = 0
        var len: Int
        var sfb: Int
        var group: Int
        var k: Int
        var idx = 0
        var x: Float
        for (g in 0 until windowGroupCount) {
            len = info.getWindowGroupLength(g)
            sfb = 0
            while (sfb < maxSFB) {
                if (sfbCB!![g][sfb] != HCB.ZERO_HCB) {
                    x = gain[index][idx]
                    group = 0
                    while (group < len) {
                        k = swbOffsets[sfb]
                        while (k < swbOffsets[sfb + 1]) {
                            data[dstOff + group * 128 + k] += x * iqData[srcOff + group * 128 + k]
                            k++
                        }
                        group++
                    }
                }
                sfb++
                idx++
            }
            dstOff += len * 128
            srcOff += len * 128
        }
    }

    companion object {
        const val BEFORE_TNS = 0
        const val AFTER_TNS = 1
        const val AFTER_IMDCT = 2
        private val CCE_SCALE = floatArrayOf(
            1.0905077f,
            1.1892071f,
            1.4142135f,
            2f
        )
    }
}
