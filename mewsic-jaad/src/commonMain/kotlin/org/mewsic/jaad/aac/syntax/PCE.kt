package org.mewsic.jaad.aac.syntax

import org.mewsic.jaad.aac.AACException
import org.mewsic.jaad.aac.Profile
import org.mewsic.jaad.aac.SampleFrequency

class PCE : Element() {
    class TaggedElement(val isIsCPE: Boolean, val tag: Int)
    class CCE(val isIsIndSW: Boolean, val tag: Int)

    var profile: Profile? = null
        private set
    lateinit var sampleFrequency: SampleFrequency
        private set
    private var frontChannelElementsCount = 0
    private var sideChannelElementsCount = 0
    private var backChannelElementsCount = 0
    private var lfeChannelElementsCount = 0
    private var assocDataElementsCount = 0
    private var validCCElementsCount = 0
    private var monoMixdown = false
    private var stereoMixdown = false
    private var matrixMixdownIDXPresent = false
    private var monoMixdownElementNumber = 0
    private var stereoMixdownElementNumber = 0
    private var matrixMixdownIDX = 0
    private var pseudoSurround = false
    private val frontElements: Array<TaggedElement?>
    private val sideElements: Array<TaggedElement?>
    private val backElements: Array<TaggedElement?>
    private val lfeElementTags: IntArray
    private val assocDataElementTags: IntArray
    private val ccElements: Array<CCE?>
    private lateinit var commentFieldData: ByteArray

    init {
        frontElements = arrayOfNulls(MAX_FRONT_CHANNEL_ELEMENTS)
        sideElements = arrayOfNulls(MAX_SIDE_CHANNEL_ELEMENTS)
        backElements = arrayOfNulls(MAX_BACK_CHANNEL_ELEMENTS)
        lfeElementTags = IntArray(MAX_LFE_CHANNEL_ELEMENTS)
        assocDataElementTags = IntArray(MAX_ASSOC_DATA_ELEMENTS)
        ccElements = arrayOfNulls(MAX_VALID_CC_ELEMENTS)
        sampleFrequency = SampleFrequency.SAMPLE_FREQUENCY_NONE
    }

    @Throws(AACException::class)
    fun decode(`in`: BitStream) {
        readElementInstanceTag(`in`)
        profile = Profile.forInt(`in`.readBits(2))
        sampleFrequency = SampleFrequency.forInt(`in`.readBits(4))
        frontChannelElementsCount = `in`.readBits(4)
        sideChannelElementsCount = `in`.readBits(4)
        backChannelElementsCount = `in`.readBits(4)
        lfeChannelElementsCount = `in`.readBits(2)
        assocDataElementsCount = `in`.readBits(3)
        validCCElementsCount = `in`.readBits(4)
        if (`in`.readBool().also { monoMixdown = it }) {
            monoMixdownElementNumber = `in`.readBits(4)
        }
        if (`in`.readBool().also { stereoMixdown = it }) {
            stereoMixdownElementNumber = `in`.readBits(4)
        }
        if (`in`.readBool().also { matrixMixdownIDXPresent = it }) {
            matrixMixdownIDX = `in`.readBits(2)
            pseudoSurround = `in`.readBool()
        }
        readTaggedElementArray(frontElements, `in`, frontChannelElementsCount)
        readTaggedElementArray(sideElements, `in`, sideChannelElementsCount)
        readTaggedElementArray(backElements, `in`, backChannelElementsCount)
        var i: Int = 0
        while (i < lfeChannelElementsCount) {
            lfeElementTags[i] = `in`.readBits(4)
            ++i
        }
        i = 0
        while (i < assocDataElementsCount) {
            assocDataElementTags[i] = `in`.readBits(4)
            ++i
        }
        i = 0
        while (i < validCCElementsCount) {
            ccElements[i] = CCE(`in`.readBool(), `in`.readBits(4))
            ++i
        }
        `in`.byteAlign()
        val commentFieldBytes = `in`.readBits(8)
        commentFieldData = ByteArray(commentFieldBytes)
        i = 0
        while (i < commentFieldBytes) {
            commentFieldData[i] = `in`.readBits(8).toByte()
            i++
        }
    }

    @Throws(AACException::class)
    private fun readTaggedElementArray(te: Array<TaggedElement?>, `in`: BitStream, len: Int) {
        for (i in 0 until len) {
            te[i] = TaggedElement(`in`.readBool(), `in`.readBits(4))
        }
    }

    val channelCount: Int
        get() = (frontChannelElementsCount + sideChannelElementsCount + backChannelElementsCount
                + lfeChannelElementsCount + assocDataElementsCount)

    companion object {
        private const val MAX_FRONT_CHANNEL_ELEMENTS = 16
        private const val MAX_SIDE_CHANNEL_ELEMENTS = 16
        private const val MAX_BACK_CHANNEL_ELEMENTS = 16
        private const val MAX_LFE_CHANNEL_ELEMENTS = 4
        private const val MAX_ASSOC_DATA_ELEMENTS = 8
        private const val MAX_VALID_CC_ELEMENTS = 16
    }
}
