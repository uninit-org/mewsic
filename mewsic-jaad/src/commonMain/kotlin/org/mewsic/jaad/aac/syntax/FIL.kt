package net.sourceforge.jaad.aac.syntax

import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.SampleFrequency

internal class FIL(private val downSampledSBR: Boolean) : Element(), Constants {
    class DynamicRangeInfo {
        val excludeMask: BooleanArray
        private val additionalExcludedChannels: BooleanArray
        var pceTagPresent = false
        var pceInstanceTag = 0
        var tagReservedBits = 0
        var excludedChannelsPresent = false
        var bandsPresent = false
        var bandsIncrement = 0
        var interpolationScheme = 0
        lateinit var bandTop: IntArray
        var progRefLevelPresent = false
        var progRefLevel = 0
        var progRefLevelReservedBits = 0
        lateinit var dynRngSgn: BooleanArray
        lateinit var dynRngCtl: IntArray

        init {
            excludeMask = BooleanArray(MAX_NBR_BANDS)
            additionalExcludedChannels = BooleanArray(MAX_NBR_BANDS)
        }

        companion object {
            private const val MAX_NBR_BANDS = 7
        }
    }

    private var dri: DynamicRangeInfo? = null

    @Throws(AACException::class)
    override fun decode(
        `in`: BitStream,
        prev: Element,
        sf: SampleFrequency,
        sbrEnabled: Boolean,
        smallFrames: Boolean
    ) {
        var count = `in`.readBits(4)
        if (count == 15) count += `in`.readBits(8) - 1
        count *= 8 //convert to bits
        val cpy = count
        val pos = `in`.position
        while (count > 0) {
            count = decodeExtensionPayload(`in`, count, prev, sf, sbrEnabled, smallFrames)
        }
        val pos2 = `in`.position - pos
        val bitsLeft = cpy - pos2
        if (bitsLeft > 0) `in`.skipBits(pos2) else if (bitsLeft < 0) throw AACException("FIL element overread: $bitsLeft")
    }

    @Throws(AACException::class)
    private fun decodeExtensionPayload(
        `in`: BitStream,
        count: Int,
        prev: Element,
        sf: SampleFrequency,
        sbrEnabled: Boolean,
        smallFrames: Boolean
    ): Int {
        val type = `in`.readBits(4)
        var ret = count - 4
        when (type) {
            TYPE_DYNAMIC_RANGE -> ret = decodeDynamicRangeInfo(`in`, ret)
            TYPE_SBR_DATA, TYPE_SBR_DATA_CRC -> {
                if (sbrEnabled) {
                    if (prev is SCE_LFE || prev is CPE || prev is CCE) {
                        prev.decodeSBR(
                            `in`,
                            sf,
                            ret,
                            prev is CPE,
                            type == TYPE_SBR_DATA_CRC,
                            downSampledSBR,
                            smallFrames
                        )
                        ret = 0
                    } else throw AACException("SBR applied on unexpected element: $prev")
                } else {
                    `in`.skipBits(ret)
                    ret = 0
                }
                `in`.skipBits(ret)
                ret = 0
            }

            TYPE_FILL, TYPE_FILL_DATA, TYPE_EXT_DATA_ELEMENT -> {
                `in`.skipBits(ret)
                ret = 0
            }

            else -> {
                `in`.skipBits(ret)
                ret = 0
            }
        }
        return ret
    }

    @Throws(AACException::class)
    private fun decodeDynamicRangeInfo(`in`: BitStream, count: Int): Int {
        if (dri == null) dri = DynamicRangeInfo()
        var ret = count
        var bandCount = 1

        //pce tag
        if (`in`.readBool().also { dri!!.pceTagPresent = it }) {
            dri!!.pceInstanceTag = `in`.readBits(4)
            dri!!.tagReservedBits = `in`.readBits(4)
        }

        //excluded channels
        if (`in`.readBool().also { dri!!.excludedChannelsPresent = it }) {
            ret -= decodeExcludedChannels(`in`)
        }

        //bands
        if (`in`.readBool().also { dri!!.bandsPresent = it }) {
            dri!!.bandsIncrement = `in`.readBits(4)
            dri!!.interpolationScheme = `in`.readBits(4)
            ret -= 8
            bandCount += dri!!.bandsIncrement
            dri!!.bandTop = IntArray(bandCount)
            for (i in 0 until bandCount) {
                dri!!.bandTop[i] = `in`.readBits(8)
                ret -= 8
            }
        }

        //prog ref level
        if (`in`.readBool().also { dri!!.progRefLevelPresent = it }) {
            dri!!.progRefLevel = `in`.readBits(7)
            dri!!.progRefLevelReservedBits = `in`.readBits(1)
            ret -= 8
        }
        dri!!.dynRngSgn = BooleanArray(bandCount)
        dri!!.dynRngCtl = IntArray(bandCount)
        for (i in 0 until bandCount) {
            dri!!.dynRngSgn[i] = `in`.readBool()
            dri!!.dynRngCtl[i] = `in`.readBits(7)
            ret -= 8
        }
        return ret
    }

    @Throws(AACException::class)
    private fun decodeExcludedChannels(`in`: BitStream): Int {
        var i: Int
        var exclChs = 0
        do {
            i = 0
            while (i < 7) {
                dri!!.excludeMask[exclChs] = `in`.readBool()
                exclChs++
                i++
            }
        } while (exclChs < 57 && `in`.readBool())
        return exclChs / 7 * 8
    }

    companion object {
        private const val TYPE_FILL = 0
        private const val TYPE_FILL_DATA = 1
        private const val TYPE_EXT_DATA_ELEMENT = 2
        private const val TYPE_DYNAMIC_RANGE = 11
        private const val TYPE_SBR_DATA = 13
        private const val TYPE_SBR_DATA_CRC = 14
    }
}
