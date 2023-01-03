package org.mewsic.jaad.aac.syntax

import org.mewsic.jaad.aac.AACException
import org.mewsic.jaad.aac.DecoderConfig
import org.mewsic.jaad.aac.Profile
import org.mewsic.jaad.aac.SampleFrequency
import org.mewsic.jaad.aac.tools.MSMask

class CPE internal constructor(frameLength: Int) : Element(), Constants {
    private var msMask: MSMask? = null
    private val msUsed: BooleanArray
    var isCommonWindow = false
        private set
    var icsL: ICStream
    var icsR: ICStream

    init {
        msUsed = BooleanArray(Constants.MAX_MS_MASK)
        icsL = ICStream(frameLength)
        icsR = ICStream(frameLength)
    }

    @Throws(AACException::class)
    override fun decode(`in`: BitStream, conf: DecoderConfig) {
        val profile: Profile = conf.getProfile()!!
        val sf: SampleFrequency = conf.getSampleFrequency()
        if (sf == SampleFrequency.SAMPLE_FREQUENCY_NONE) throw AACException("invalid sample frequency")
        readElementInstanceTag(`in`)
        isCommonWindow = `in`.readBool()
        val info: ICSInfo = icsL.info
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
            if (`in`.readBool().also { info.isLTPrediction2Present = it }) info.lTPrediction2!!.decode(
                `in`,
                info,
                profile
            )
        }
        icsL.decode(`in`, isCommonWindow, conf)
        icsR.decode(`in`, isCommonWindow, conf)
    }

    val leftChannel: ICStream
        get() = icsL
    val rightChannel: ICStream
        get() = icsR
    val mSMask: MSMask?
        get() = msMask

    fun isMSUsed(off: Int): Boolean {
        return msUsed[off]
    }

    val isMSMaskPresent: Boolean
        get() = msMask!! != MSMask.TYPE_ALL_0
}
