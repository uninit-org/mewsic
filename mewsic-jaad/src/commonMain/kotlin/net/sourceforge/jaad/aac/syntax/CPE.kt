package net.sourceforge.jaad.aac.syntax

import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.DecoderConfig
import net.sourceforge.jaad.aac.Profile
import net.sourceforge.jaad.aac.SampleFrequency
import net.sourceforge.jaad.aac.tools.MSMask

class CPE internal constructor(frameLength: Int) : net.sourceforge.jaad.aac.syntax.Element(), Constants {
    private var msMask: MSMask? = null
    private val msUsed: BooleanArray
    var isCommonWindow = false
        private set
    var icsL: net.sourceforge.jaad.aac.syntax.ICStream
    var icsR: net.sourceforge.jaad.aac.syntax.ICStream

    init {
        msUsed = BooleanArray(Constants.MAX_MS_MASK)
        icsL = net.sourceforge.jaad.aac.syntax.ICStream(frameLength)
        icsR = net.sourceforge.jaad.aac.syntax.ICStream(frameLength)
    }

    @Throws(AACException::class)
    fun decode(`in`: BitStream, conf: DecoderConfig) {
        val profile: Profile = conf.getProfile()!!
        val sf: SampleFrequency = conf.getSampleFrequency()
        if (sf == SampleFrequency.SAMPLE_FREQUENCY_NONE) throw AACException("invalid sample frequency")
        readElementInstanceTag(`in`)
        isCommonWindow = `in`.readBool()
        val info: net.sourceforge.jaad.aac.syntax.ICSInfo = icsL.info
        if (isCommonWindow) {
            info.decode(`in`, conf, isCommonWindow)

            icsR.info.setData(info)
            msMask = MSMask.forInt(`in`.readBits(2))
            if (msMask!! == MSMask.TYPE_USED) {
                val maxSFB: Int = info.maxSFB
                val windowGroupCount: Int = info.windowGroupCount
                for (idx in 0 until windowGroupCount * maxSFB) {
                    msUsed[idx] = `in`.readBool()
                }
            } else if (msMask!! == MSMask.TYPE_ALL_1) msUsed.fill(true) else if (msMask!! == MSMask.TYPE_ALL_0
            ) msUsed.fill(false) else throw AACException("reserved MS mask type used")
        } else {
            msMask = MSMask.TYPE_ALL_0
            msUsed.fill(false)
        }
        if (profile.isErrorResilientProfile && info.isLTPrediction1Present) {
            if (`in`.readBool().also { info.isLTPrediction2Present = it }) info.lTPrediction2!!.decode(`in`, info, profile)
        }
        icsL.decode(`in`, isCommonWindow, conf)
        icsR.decode(`in`, isCommonWindow, conf)
    }

    val leftChannel: net.sourceforge.jaad.aac.syntax.ICStream
        get() = icsL
    val rightChannel: net.sourceforge.jaad.aac.syntax.ICStream
        get() = icsR
    val mSMask: MSMask?
        get() = msMask

    fun isMSUsed(off: Int): Boolean {
        return msUsed[off]
    }

    val isMSMaskPresent: Boolean
        get() = msMask!! != MSMask.TYPE_ALL_0
}
