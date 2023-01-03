package net.sourceforge.jaad.aac.syntax

import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.DecoderConfig
import net.sourceforge.jaad.aac.Profile
import net.sourceforge.jaad.aac.SampleFrequency
import net.sourceforge.jaad.aac.syntax.ScaleFactorBands.Companion.SWB_LONG_WINDOW_COUNT
import net.sourceforge.jaad.aac.syntax.ScaleFactorBands.Companion.SWB_OFFSET_LONG_WINDOW
import net.sourceforge.jaad.aac.syntax.ScaleFactorBands.Companion.SWB_OFFSET_SHORT_WINDOW
import net.sourceforge.jaad.aac.syntax.ScaleFactorBands.Companion.SWB_SHORT_WINDOW_COUNT
import net.sourceforge.jaad.aac.tools.ICPrediction
import net.sourceforge.jaad.aac.tools.LTPrediction


class ICSInfo(private val frameLength: Int) : Constants, ScaleFactorBands {
    enum class WindowSequence {
        ONLY_LONG_SEQUENCE, LONG_START_SEQUENCE, EIGHT_SHORT_SEQUENCE, LONG_STOP_SEQUENCE;

        companion object {
            @Throws(AACException::class)
            fun forInt(i: Int): WindowSequence {
                val w: WindowSequence
                w = when (i) {
                    0 -> ONLY_LONG_SEQUENCE
                    1 -> LONG_START_SEQUENCE
                    2 -> EIGHT_SHORT_SEQUENCE
                    3 -> LONG_STOP_SEQUENCE
                    else -> throw AACException("unknown window sequence type")
                }
                return w
            }
        }
    }

    lateinit var windowSequence: WindowSequence
        private set
    private val windowShape: IntArray = IntArray(2)
    /* =========== gets ============ */  var maxSFB = 0
        private set

    //prediction
    var isICPredictionPresent = false
        private set
    private var icPredict: ICPrediction? = null
    var isLTPrediction1Present = false
    var isLTPrediction2Present = false
    private var ltPredict1: LTPrediction? = null
    private var ltPredict2: LTPrediction? = null

    //windows/sfbs
    var windowCount = 0
        private set
    var windowGroupCount = 0
        private set
    private var windowGroupLength: IntArray
    var sWBCount = 0
        private set
    var sWBOffsets: IntArray = IntArray(0)
        private set

    init {
        windowSequence = WindowSequence.ONLY_LONG_SEQUENCE
        windowGroupLength = IntArray(Constants.MAX_WINDOW_GROUP_COUNT)
    }

    /* ========== decoding ========== */
    @Throws(AACException::class)
    override fun decode(`in`: BitStream, conf: DecoderConfig, commonWindow: Boolean) {
        val sf: SampleFrequency = conf.getSampleFrequency()
        if (sf == SampleFrequency.SAMPLE_FREQUENCY_NONE) throw AACException("invalid sample frequency")
        `in`.skipBit() //reserved
        windowSequence = WindowSequence.forInt(`in`.readBits(2))
        windowShape[PREVIOUS] = windowShape[CURRENT]
        windowShape[CURRENT] = `in`.readBit()
        windowGroupCount = 1
        windowGroupLength[0] = 1
        if (windowSequence == WindowSequence.EIGHT_SHORT_SEQUENCE) {
            maxSFB = `in`.readBits(4)
            var i: Int
            i = 0
            while (i < 7) {
                if (`in`.readBool()) windowGroupLength[windowGroupCount - 1]++ else {
                    windowGroupCount++
                    windowGroupLength[windowGroupCount - 1] = 1
                }
                i++
            }
            windowCount = 8
            sWBOffsets = SWB_OFFSET_SHORT_WINDOW.get(sf.index)
            sWBCount = SWB_SHORT_WINDOW_COUNT.get(sf.index)
            isICPredictionPresent = false
        } else {
            maxSFB = `in`.readBits(6)
            windowCount = 1
            sWBOffsets = SWB_OFFSET_LONG_WINDOW.get(sf.index)
            sWBCount = SWB_LONG_WINDOW_COUNT.get(sf.index)
            isICPredictionPresent = `in`.readBool()
            if (isICPredictionPresent) readPredictionData(`in`, conf.getProfile()!!, sf, commonWindow)
        }
    }

    @Throws(AACException::class)
    private fun readPredictionData(`in`: BitStream, profile: Profile, sf: SampleFrequency, commonWindow: Boolean) {
        when (profile) {
            Profile.AAC_MAIN -> {
                if (icPredict == null) icPredict = ICPrediction()
                icPredict!!.decode(`in`, maxSFB, sf)
            }

            Profile.AAC_LTP -> {
                if (`in`.readBool().also { isLTPrediction1Present = it }) {
                    if (ltPredict1 == null) ltPredict1 = LTPrediction(frameLength)
                    ltPredict1!!.decode(`in`, this, profile)
                }
                if (commonWindow) {
                    if (`in`.readBool().also { isLTPrediction2Present = it }) {
                        if (ltPredict2 == null) ltPredict2 = LTPrediction(frameLength)
                        ltPredict2!!.decode(`in`, this, profile)
                    }
                }
            }

            Profile.ER_AAC_LTP -> if (!commonWindow) {
                if (`in`.readBool().also { isLTPrediction1Present = it }) {
                    if (ltPredict1 == null) ltPredict1 = LTPrediction(frameLength)
                    ltPredict1!!.decode(`in`, this, profile)
                }
            }

            else -> throw AACException("unexpected profile for LTP: $profile")
        }
    }

    val sWBOffsetMax: Int
        get() = sWBOffsets[sWBCount]

    fun getWindowGroupLength(g: Int): Int {
        return windowGroupLength[g]
    }

    val isEightShortFrame: Boolean
        get() = windowSequence == WindowSequence.EIGHT_SHORT_SEQUENCE

    fun getWindowShape(index: Int): Int {
        return windowShape[index]
    }

    val iCPrediction: ICPrediction?
        get() = icPredict
    val lTPrediction1: LTPrediction?
        get() = ltPredict1
    val lTPrediction2: LTPrediction?
        get() = ltPredict2

    fun unsetPredictionSFB(sfb: Int) {
        if (isICPredictionPresent) icPredict!!.setPredictionUnused(sfb)
        if (isLTPrediction1Present) ltPredict1!!.setPredictionUnused(sfb)
        if (isLTPrediction2Present) ltPredict2!!.setPredictionUnused(sfb)
    }

    fun setData(info: ICSInfo) {
        windowSequence = WindowSequence.valueOf(info.windowSequence.name)
        windowShape[PREVIOUS] = windowShape[CURRENT]
        windowShape[CURRENT] = info.windowShape[CURRENT]
        maxSFB = info.maxSFB
        isICPredictionPresent = info.isICPredictionPresent
        if (isICPredictionPresent) icPredict = info.icPredict
        isLTPrediction1Present = info.isLTPrediction1Present
        if (isLTPrediction1Present) {
            ltPredict1!!.copy(info.ltPredict1!!)
            ltPredict2!!.copy(info.ltPredict2!!)
        }
        windowCount = info.windowCount
        windowGroupCount = info.windowGroupCount
        windowGroupLength = info.windowGroupLength.copyOf()
        sWBCount = info.sWBCount
        sWBOffsets = info.sWBOffsets.copyOf()
    }

    companion object {
        const val WINDOW_SHAPE_SINE = 0
        const val WINDOW_SHAPE_KAISER = 1
        const val PREVIOUS = 0
        const val CURRENT = 1
    }
}
