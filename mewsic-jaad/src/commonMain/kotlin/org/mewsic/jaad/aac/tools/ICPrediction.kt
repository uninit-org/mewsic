package org.mewsic.jaad.aac.tools

import org.mewsic.jaad.aac.AACException
import org.mewsic.jaad.aac.SampleFrequency
import org.mewsic.jaad.aac.syntax.*
import kotlin.math.*

/**
 * Intra-channel prediction used in profile Main
 * @author in-somnia
 */
class ICPrediction {
    private var predictorReset = false
    private var predictorResetGroup = 0
    private var predictionUsed: BooleanArray = BooleanArray(0)
    private var states: Array<PredictorState?> = arrayOfNulls(MAX_PREDICTORS)

    private class PredictorState {
        var cor0 = 0.0f
        var cor1 = 0.0f
        var var0 = 0.0f
        var var1 = 0.0f
        var r0 = 1.0f
        var r1 = 1.0f
    }

    init {
        resetAllPredictors()
    }

    @Throws(AACException::class)
    override fun decode(`in`: BitStream, maxSFB: Int, sf: SampleFrequency) {
        val predictorCount = sf.predictorCount
        if (`in`.readBool().also { predictorReset = it }) predictorResetGroup = `in`.readBits(5)
        val maxPredSFB = sf.maximalPredictionSFB
        val length: Int = min(maxSFB, maxPredSFB)
        predictionUsed = BooleanArray(length)
        for (sfb in 0 until length) {
            predictionUsed[sfb] = `in`.readBool()
        }
//        Constants.LOGGER.log(
//            java.util.logging.Level.WARNING,
//            "ICPrediction: maxSFB={0}, maxPredSFB={1}",
//            intArrayOf(maxSFB, maxPredSFB)
//        )
        /*//if maxSFB<maxPredSFB set remaining to false
		for(int sfb = length; sfb<maxPredSFB; sfb++) {
		predictionUsed[sfb] = false;
		}*/
    }

    fun setPredictionUnused(sfb: Int) {
        predictionUsed[sfb] = false
    }

    fun process(ics: ICStream, data: FloatArray, sf: SampleFrequency) {
        val info = ics.info
        if (info.isEightShortFrame) resetAllPredictors() else {
            val len: Int = min(sf.maximalPredictionSFB, info.maxSFB)
            val swbOffsets = info.sWBOffsets
            var k: Int
            for (sfb in 0 until len) {
                k = swbOffsets[sfb]
                while (k < swbOffsets[sfb + 1]) {
                    predict(data, k, predictionUsed[sfb])
                    k++
                }
            }
            if (predictorReset) resetPredictorGroup(predictorResetGroup)
        }
    }

    private fun resetPredictState(index: Int) {
        if (states[index] == null) states[index] = PredictorState()
        states[index]!!.r0 = 0f
        states[index]!!.r1 = 0f
        states[index]!!.cor0 = 0f
        states[index]!!.cor1 = 0f
        states[index]!!.var0 = (0x380).toFloat()
        states[index]!!.var1 = (0x380).toFloat()
    }

    private fun resetAllPredictors() {
        var i: Int
        i = 0
        while (i < states.size) {
            resetPredictState(i)
            i++
        }
    }

    private fun resetPredictorGroup(group: Int) {
        var i: Int
        i = group - 1
        while (i < states.size) {
            resetPredictState(i)
            i += 30
        }
    }

    private fun predict(data: FloatArray, off: Int, output: Boolean) {
        if (states[off] == null) states[off] = PredictorState()
        val state = states[off]
        val r0 = state!!.r0
        val r1 = state.r1
        val cor0 = state.cor0
        val cor1 = state.cor1
        val var0 = state.var0
        val var1 = state.var1
        val k1: Float = if (var0 > 1) cor0 * even(A / var0) else 0f
        val k2: Float = if (var1 > 1) cor1 * even(A / var1) else 0f
        val pv = round(k1 * r0 + k2 * r1)
        if (output) data[off] += pv * SF_SCALE
        val e0 = data[off] * INV_SF_SCALE
        val e1 = e0 - k1 * r0
        state.cor1 = trunc(ALPHA * cor1 + r1 * e1)
        state.var1 = trunc(ALPHA * var1 + 0.5f * (r1 * r1 + e1 * e1))
        state.cor0 = trunc(ALPHA * cor0 + r0 * e0)
        state.var0 = trunc(ALPHA * var0 + 0.5f * (r0 * r0 + e0 * e0))
        state.r1 = trunc(A * (r0 - k1 * e0))
        state.r0 = trunc(A * e0)
    }

    private fun round(pf: Float): Float {
        return Float.intBitsToFloat(Float.floatToIntBits(pf) + 0x00008000 and -0x10000)
    }

    private fun even(pf: Float): Float {
        var i: Int = Float.floatToIntBits(pf)
        i = i + 0x00007FFF + (i and (0x00010000 shr 16)) and -0x10000
        return Float.intBitsToFloat(i)
    }

    private fun trunc(pf: Float): Float {
        return Float.intBitsToFloat(Float.floatToIntBits(pf) and -0x10000)
    }

    companion object {
        private const val SF_SCALE = 1.0f / -1024.0f
        private const val INV_SF_SCALE = 1.0f / SF_SCALE
        private const val MAX_PREDICTORS = 672
        private const val A = 0.953125f //61.0 / 64
        private const val ALPHA = 0.90625f //29.0 / 32
    }
}

// FIXME: move this to commons asap, also assure this works
fun Float.Companion.intBitsToFloat(bits: Int): Float = Float.fromBits(bits)
fun Float.Companion.floatToIntBits(value: Float): Int = value.toRawBits()
