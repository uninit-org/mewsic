package org.mewsic.jaad.aac.tools

import org.mewsic.jaad.aac.AACException
import org.mewsic.jaad.aac.Profile
import org.mewsic.jaad.aac.SampleFrequency
import org.mewsic.jaad.aac.filterbank.FilterBank
import org.mewsic.jaad.aac.syntax.*
import org.mewsic.commons.lang.Arrays
import kotlin.math.*

/**
 * Long-term prediction
 * @author in-somnia
 */
class LTPrediction(private val frameLength: Int) : Constants {
    private val states: IntArray
    private var coef = 0
    private var lag = 0
    private var lastBand = 0
    private var lagUpdate = false
    private var shortUsed: BooleanArray = BooleanArray(0)
    private var shortLagPresent: BooleanArray = BooleanArray(0)
    private var longUsed: BooleanArray? = null
    private var shortLag: IntArray = IntArray(0)

    init {
        states = IntArray(4 * frameLength)
    }

    @Throws(AACException::class)
    fun decode(`in`: BitStream, info: ICSInfo, profile: Profile) {
        lag = 0
        if (profile == Profile.AAC_LD) {
            lagUpdate = `in`.readBool()
            if (lagUpdate) lag = `in`.readBits(10)
        } else lag = `in`.readBits(11)
        if (lag > frameLength shl 1) throw AACException("LTP lag too large: $lag")
        coef = `in`.readBits(3)
        val windowCount = info.windowCount
        if (info.isEightShortFrame) {
            shortUsed = BooleanArray(windowCount)
            shortLagPresent = BooleanArray(windowCount)
            shortLag = IntArray(windowCount)
            for (w in 0 until windowCount) {
                if (`in`.readBool().also { shortUsed[w] = it }) {
                    shortLagPresent[w] = `in`.readBool()
                    if (shortLagPresent[w]) shortLag[w] = `in`.readBits(4)
                }
            }
        } else {
            lastBand = min(info.maxSFB, Constants.MAX_LTP_SFB)
            longUsed = BooleanArray(lastBand)
            for (i in 0 until lastBand) {
                longUsed!![i] = `in`.readBool()
            }
        }
    }

    fun setPredictionUnused(sfb: Int) {
        if (longUsed != null) longUsed!![sfb] = false
    }

    fun process(ics: ICStream, data: FloatArray, filterBank: FilterBank, sf: SampleFrequency?) {
        val info = ics.info
        if (!info.isEightShortFrame) {
            val samples = frameLength shl 1
            val `in` = FloatArray(2048)
            val out = FloatArray(2048)
            for (i in 0 until samples) {
                `in`[i] = states[samples + i - lag] * CODEBOOK[coef]
            }
            filterBank.processLTP(
                info.windowSequence, info.getWindowShape(ICSInfo.CURRENT),
                info.getWindowShape(ICSInfo.PREVIOUS), `in`, out
            )
            if (ics.isTNSDataPresent) ics.tNS?.process(ics, out, sf, true)
            val swbOffsets = info.sWBOffsets
            val swbOffsetMax = info.sWBOffsetMax
            var low: Int
            var high: Int
            var bin: Int
            for (sfb in 0 until lastBand) {
                if (longUsed!![sfb]) {
                    low = swbOffsets[sfb]
                    high = min(swbOffsets[sfb + 1], swbOffsetMax)
                    bin = low
                    while (bin < high) {
                        data[bin] += out[bin]
                        bin++
                    }
                }
            }
        }
    }

    fun updateState(time: FloatArray, overlap: FloatArray, profile: Profile) {
        var i: Int
        if (profile == Profile.AAC_LD) {
            i = 0
            while (i < frameLength) {
                states[i] = states[i + frameLength]
                states[frameLength + i] = states[i + frameLength * 2]
                states[frameLength * 2 + i] = round(time[i]).toInt()
                states[frameLength * 3 + i] = round(overlap[i]).toInt()
                i++
            }
        } else {
            i = 0
            while (i < frameLength) {
                states[i] = states[i + frameLength]
                states[frameLength + i] = round(time[i]).toInt()
                states[frameLength * 2 + i] = round(overlap[i]).toInt()
                i++
            }
        }
    }

    fun copy(ltp: LTPrediction) {
        Arrays.arraycopy(ltp.states, 0, states, 0, states.size)
        coef = ltp.coef
        lag = ltp.lag
        lastBand = ltp.lastBand
        lagUpdate = ltp.lagUpdate
        shortUsed = Arrays.copyOf(ltp.shortUsed, ltp.shortUsed.size)
        shortLagPresent = Arrays.copyOf(ltp.shortLagPresent, ltp.shortLagPresent.size)
        shortLag = Arrays.copyOf(ltp.shortLag, ltp.shortLag.size)
        longUsed = ltp.longUsed?.let { Arrays.copyOf(it, ltp.longUsed!!.size) }
    }

    companion object {
        private val CODEBOOK = floatArrayOf(
            0.570829f,
            0.696616f,
            0.813004f,
            0.911304f,
            0.984900f,
            1.067894f,
            1.194601f,
            1.369533f
        )

        fun isLTPProfile(profile: Profile): Boolean {
            return profile == Profile.AAC_LTP || profile == Profile.ER_AAC_LTP || profile == Profile.AAC_LD
        }
    }
}
