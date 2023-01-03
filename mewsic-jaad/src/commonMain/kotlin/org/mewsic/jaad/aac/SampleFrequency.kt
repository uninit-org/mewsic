package net.sourceforge.jaad.aac

import kotlin.jvm.JvmStatic

/**
 * An enumeration that represents all possible sample frequencies AAC data can
 * have.
 *
 * @author in-somnia
 */
enum class SampleFrequency(
    /**
     * Returns this sample frequency's index between 0 (96000) and 11 (8000)
     * or -1 if this is SAMPLE_FREQUENCY_NONE.
     * @return the sample frequency's index
     */
    val index: Int,
    /**
     * Returns the sample frequency as integer value. This may be a value
     * between 96000 and 8000, or 0 if this is SAMPLE_FREQUENCY_NONE.
     * @return the sample frequency
     */
    val frequency: Int, private val prediction: IntArray, private val maxTNS_SFB: IntArray
) {
    SAMPLE_FREQUENCY_96000(0, 96000, intArrayOf(33, 512), intArrayOf(31, 9)), SAMPLE_FREQUENCY_88200(
        1,
        88200,
        intArrayOf(33, 512),
        intArrayOf(31, 9)
    ),
    SAMPLE_FREQUENCY_64000(2, 64000, intArrayOf(38, 664), intArrayOf(34, 10)), SAMPLE_FREQUENCY_48000(
        3,
        48000,
        intArrayOf(40, 672),
        intArrayOf(40, 14)
    ),
    SAMPLE_FREQUENCY_44100(4, 44100, intArrayOf(40, 672), intArrayOf(42, 14)), SAMPLE_FREQUENCY_32000(
        5,
        32000,
        intArrayOf(40, 672),
        intArrayOf(51, 14)
    ),
    SAMPLE_FREQUENCY_24000(6, 24000, intArrayOf(41, 652), intArrayOf(46, 14)), SAMPLE_FREQUENCY_22050(
        7,
        22050,
        intArrayOf(41, 652),
        intArrayOf(46, 14)
    ),
    SAMPLE_FREQUENCY_16000(8, 16000, intArrayOf(37, 664), intArrayOf(42, 14)), SAMPLE_FREQUENCY_12000(
        9,
        12000,
        intArrayOf(37, 664),
        intArrayOf(42, 14)
    ),
    SAMPLE_FREQUENCY_11025(10, 11025, intArrayOf(37, 664), intArrayOf(42, 14)), SAMPLE_FREQUENCY_8000(
        11,
        8000,
        intArrayOf(34, 664),
        intArrayOf(39, 14)
    ),
    SAMPLE_FREQUENCY_NONE(-1, 0, intArrayOf(0, 0), intArrayOf(0, 0));

    val maximalPredictionSFB: Int
        /**
         * Returns the highest scale factor band allowed for ICPrediction at this
         * sample frequency.
         * This method is mainly used internally.
         * @return the highest prediction SFB
         */
        get() = prediction[0]
    val predictorCount: Int
        /**
         * Returns the number of predictors allowed for ICPrediction at this
         * sample frequency.
         * This method is mainly used internally.
         * @return the number of ICPredictors
         */
        get() = prediction[1]

    /**
     * Returns the highest scale factor band allowed for TNS at this
     * sample frequency.
     * This method is mainly used internally.
     * @return the highest SFB for TNS
     */
    fun getMaximalTNS_SFB(shortWindow: Boolean): Int {
        return maxTNS_SFB[if (shortWindow) 1 else 0]
    }

    /**
     * Returns a string representation of this sample frequency.
     * The method is identical to `getDescription()`.
     * @return the sample frequency's description
     */
    override fun toString(): String {
        return frequency.toString()
    }

    companion object {
        /**
         * Returns a sample frequency instance for the given index. If the index
         * is not between 0 and 11 inclusive, SAMPLE_FREQUENCY_NONE is returned.
         * @return a sample frequency with the given index
         */
        @JvmStatic
        fun forInt(i: Int): SampleFrequency {
            val freq: SampleFrequency = if (i >= 0 && i < 12) values()[i] else SAMPLE_FREQUENCY_NONE
            return freq
        }

        @JvmStatic
        fun forFrequency(i: Int): SampleFrequency {
            val all = values()
            var freq: SampleFrequency? = null
            var j = 0
            while (freq == null && j < 12) {
                if (i == all[j].frequency) freq = all[j]
                j++
            }
            if (freq == null) freq = SAMPLE_FREQUENCY_NONE
            return freq
        }
    }
}
