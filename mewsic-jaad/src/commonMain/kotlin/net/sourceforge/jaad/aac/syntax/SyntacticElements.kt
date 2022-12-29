package net.sourceforge.jaad.aac.syntax

import net.sourceforge.jaad.aac.*
import net.sourceforge.jaad.aac.filterbank.FilterBank
import net.sourceforge.jaad.aac.sbr.SBR
import net.sourceforge.jaad.aac.tools.LTPrediction
import kotlin.math.*
import net.sourceforge.jaad.aac.tools.IS
import net.sourceforge.jaad.aac.tools.MS

class SyntacticElements(config: DecoderConfig) : Constants {
    //global properties
    private val config: DecoderConfig
    private var sbrPresent = false
    private var psPresent = false
    private var bitsRead = 0

    //elements
    private val pce: PCE
    private val elements //SCE, LFE and CPE
            : Array<Element?>
    private val cces: Array<CCE?>
    private val dses: Array<DSE?>
    private val fils: Array<FIL?>
    private var curElem = 0
    private var curCCE = 0
    private var curDSE = 0
    private var curFIL = 0
    private var data: Array<FloatArray>? = null

    init {
        this.config = config
        pce = PCE()
        elements = arrayOfNulls(4 * Constants.MAX_ELEMENTS)
        cces = arrayOfNulls(Constants.MAX_ELEMENTS)
        dses = arrayOfNulls(Constants.MAX_ELEMENTS)
        fils = arrayOfNulls(Constants.MAX_ELEMENTS)
        startNewFrame()
    }

    fun startNewFrame() {
        curElem = 0
        curCCE = 0
        curDSE = 0
        curFIL = 0
        sbrPresent = false
        psPresent = false
        bitsRead = 0
    }

    @Throws(AACException::class)
    fun decode(`in`: BitStream) {
        val start = `in`.position //should be 0
        var type: Int
        var prev: Element? = null
        var content = true
        if (!config.getProfile()!!.isErrorResilientProfile) {
            while (content) {
                // commented out  && `in`.readBits(3).also { type = it } != Constants.ELEMENT_END
                if (content) {
                    val b = `in`.readBits(3)
                    type = b
                    if (b == Constants.ELEMENT_END) {
                        break
                    }
                } else {
                    break
                }
                when (type) {
                    Constants.ELEMENT_SCE, Constants.ELEMENT_LFE -> {
                        prev = decodeSCE_LFE(`in`)
                    }

                    Constants.ELEMENT_CPE -> {
                        prev = decodeCPE(`in`)
                    }

                    Constants.ELEMENT_CCE -> {
                        decodeCCE(`in`)
                        prev = null
                    }

                    Constants.ELEMENT_DSE -> {
                        decodeDSE(`in`)
                        prev = null
                    }

                    Constants.ELEMENT_PCE -> {
                        decodePCE(`in`)
                        prev = null
                    }

                    Constants.ELEMENT_FIL -> {
                        decodeFIL(`in`, prev)
                        prev = null
                    }
                }
            }
            content = false
            prev = null
        } else {
            //error resilient raw data block
            when (config.getChannelConfiguration()) {
                ChannelConfiguration.CHANNEL_CONFIG_MONO -> decodeSCE_LFE(`in`)
                ChannelConfiguration.CHANNEL_CONFIG_STEREO -> decodeCPE(`in`)
                ChannelConfiguration.CHANNEL_CONFIG_STEREO_PLUS_CENTER -> {
                    decodeSCE_LFE(`in`)
                    decodeCPE(`in`)
                }

                ChannelConfiguration.CHANNEL_CONFIG_STEREO_PLUS_CENTER_PLUS_REAR_MONO -> {
                    decodeSCE_LFE(`in`)
                    decodeCPE(`in`)
                    decodeSCE_LFE(`in`)
                }

                ChannelConfiguration.CHANNEL_CONFIG_FIVE -> {
                    decodeSCE_LFE(`in`)
                    decodeCPE(`in`)
                    decodeCPE(`in`)
                }

                ChannelConfiguration.CHANNEL_CONFIG_FIVE_PLUS_ONE -> {
                    decodeSCE_LFE(`in`)
                    decodeCPE(`in`)
                    decodeCPE(`in`)
                    decodeSCE_LFE(`in`)
                }

                ChannelConfiguration.CHANNEL_CONFIG_SEVEN_PLUS_ONE -> {
                    decodeSCE_LFE(`in`)
                    decodeCPE(`in`)
                    decodeCPE(`in`)
                    decodeCPE(`in`)
                    decodeSCE_LFE(`in`)
                }

                else -> throw AACException("unsupported channel configuration for error resilience: " + config.getChannelConfiguration())
            }
        }
        `in`.byteAlign()
        bitsRead = `in`.position - start
    }

    @Throws(AACException::class)
    private fun decodeSCE_LFE(`in`: BitStream): Element? {
        if (elements[curElem] == null) elements[curElem] = SCE_LFE(config.frameLength)
        (elements[curElem] as SCE_LFE?)!!.decode(`in`, config)
        curElem++
        return elements[curElem - 1]
    }

    @Throws(AACException::class)
    private fun decodeCPE(`in`: BitStream): Element? {
        if (elements[curElem] == null) elements[curElem] = CPE(config.frameLength)
        (elements[curElem] as CPE?)!!.decode(`in`, config)
        curElem++
        return elements[curElem - 1]
    }

    @Throws(AACException::class)
    private fun decodeCCE(`in`: BitStream) {
        if (curCCE == Constants.MAX_ELEMENTS) throw AACException("too much CCE elements")
        if (cces[curCCE] == null) cces[curCCE] = CCE(config.frameLength)
        cces[curCCE]!!.decode(`in`, config)
        curCCE++
    }

    @Throws(AACException::class)
    private fun decodeDSE(`in`: BitStream) {
        if (curDSE == Constants.MAX_ELEMENTS) throw AACException("too much CCE elements")
        if (dses[curDSE] == null) dses[curDSE] = DSE()
        dses[curDSE]!!.decode(`in`)
        curDSE++
    }

    @Throws(AACException::class)
    private fun decodePCE(`in`: BitStream) {
        pce.decode(`in`)
        config.setProfile(pce.profile)
        config.setSampleFrequency(pce.sampleFrequency)
        config.setChannelConfiguration(ChannelConfiguration.forInt(pce.channelCount))
    }

    @Throws(AACException::class)
    private fun decodeFIL(`in`: BitStream, prev: Element?) {
        if (curFIL == Constants.MAX_ELEMENTS) throw AACException("too much FIL elements")
        if (fils[curFIL] == null) fils[curFIL] = FIL(config.isSBRDownSampled)
        fils[curFIL]!!.decode(`in`, prev!!, config.getSampleFrequency(), config.isSBREnabled, config.isSmallFrameUsed)
        curFIL++
        if (prev != null && prev.isSBRPresent) {
            sbrPresent = true
            if (!psPresent && prev.sBR!!.isPSUsed) psPresent = true
        }
    }

    @Throws(AACException::class)
    fun process(filterBank: FilterBank) {
        val profile: Profile? = config.getProfile()
        val sf: SampleFrequency = config.getSampleFrequency()
        //final ChannelConfiguration channels = config.getChannelConfiguration();
        var chs: Int = config.getChannelConfiguration().channelCount
        if (chs == 1 && psPresent) chs++
        val mult = if (sbrPresent) 2 else 1
        //only reallocate if needed
        if (data == null || chs != data!!.size || mult * config.frameLength != data!![0].size) data =
            Array(chs) { FloatArray(mult * config.frameLength) }
        var channel = 0
        var e: Element?
        var scelfe: SCE_LFE
        var cpe: CPE
        var i = 0
        while (i < elements.size && channel < chs) {
            e = elements[i]
            if (e == null) {
                i++
                continue
            }
            if (e is SCE_LFE) {
                scelfe = e
                channel += processSingle(scelfe, filterBank, channel, profile!!, sf)
            } else if (e is CPE) {
                cpe = e
                processPair(cpe, filterBank, channel, profile!!, sf)
                channel += 2
            } else if (e is CCE) {
                //applies invquant and save the result in the CCE
                e.process()
                channel++
            }
            i++
        }
    }

    @Throws(AACException::class)
    private fun processSingle(
        scelfe: SCE_LFE,
        filterBank: FilterBank,
        channel: Int,
        profile: Profile,
        sf: SampleFrequency
    ): Int {
        val ics = scelfe.iCStream
        val info = ics.info
        val ltp: LTPrediction? = info.lTPrediction1
        val elementID = scelfe.elementInstanceTag

        //inverse quantization
        val iqData = ics.invQuantData

        //prediction
        if (profile == Profile.AAC_MAIN && info.isICPredictionPresent) info.iCPrediction!!.process(ics, iqData, sf)
        if (LTPrediction.isLTPProfile(profile) && info.isLTPrediction1Present) ltp!!.process(ics, iqData, filterBank, sf)

        //dependent coupling
        processDependentCoupling(false, elementID, CCE.BEFORE_TNS, iqData, null)

        //TNS
        if (ics.isTNSDataPresent) ics.tNS!!.process(ics, iqData, sf, false)

        //dependent coupling
        processDependentCoupling(false, elementID, CCE.AFTER_TNS, iqData, null)

        //filterbank
        filterBank.process(
            info.windowSequence,
            info.getWindowShape(ICSInfo.CURRENT),
            info.getWindowShape(ICSInfo.PREVIOUS),
            iqData,
            data!![channel],
            channel
        )
        if (LTPrediction.isLTPProfile(profile)) ltp!!.updateState(
            data!![channel],
            filterBank.getOverlap(channel),
            profile
        )

        //dependent coupling
        processIndependentCoupling(false, elementID, data!![channel], null)

        //gain control
        if (ics.isGainControlPresent) ics.getGainControl()!!.process(
            iqData,
            info.getWindowShape(ICSInfo.CURRENT),
            info.getWindowShape(ICSInfo.PREVIOUS),
            info.windowSequence
        )

        //SBR
        var chs = 1
        if (sbrPresent && config.isSBREnabled) {
            val sbr: SBR? = scelfe.sBR
            if (sbr?.isPSUsed == true) {
                chs = 2
                scelfe.sBR!!.process(data!![channel], data!![channel + 1], false)
            } else scelfe.sBR!!.process(data!![channel], false)
        }
        return chs
    }

    @Throws(AACException::class)
    private fun processPair(cpe: CPE, filterBank: FilterBank, channel: Int, profile: Profile, sf: SampleFrequency) {
        val ics1 = cpe.leftChannel
        val ics2 = cpe.rightChannel
        val info1 = ics1.info
        val info2 = ics2.info
        val ltp1: LTPrediction? = info1.lTPrediction1
        val ltp2: LTPrediction = if (cpe.isCommonWindow) info1.lTPrediction2!! else info2.lTPrediction1!!
        val elementID = cpe.elementInstanceTag

        //inverse quantization
        val iqData1 = ics1.invQuantData
        val iqData2 = ics2.invQuantData

        //MS
        if (cpe.isCommonWindow && cpe.isMSMaskPresent) MS.process(cpe, iqData1, iqData2)
        //main prediction
        if (profile == Profile.AAC_MAIN) {
            if (info1.isICPredictionPresent) info1.iCPrediction!!.process(ics1, iqData1, sf)
            if (info2.isICPredictionPresent) info2.iCPrediction!!.process(ics2, iqData2, sf)
        }
        //IS
        IS.process(cpe, iqData1, iqData2)

        //LTP
        if (LTPrediction.isLTPProfile(profile)) {
            if (info1.isLTPrediction1Present) ltp1!!.process(ics1, iqData1, filterBank, sf)
            if (cpe.isCommonWindow && info1.isLTPrediction2Present) ltp2.process(
                ics2,
                iqData2,
                filterBank,
                sf
            ) else if (info2.isLTPrediction1Present) ltp2.process(ics2, iqData2, filterBank, sf)
        }

        //dependent coupling
        processDependentCoupling(true, elementID, CCE.BEFORE_TNS, iqData1, iqData2)

        //TNS
        if (ics1.isTNSDataPresent) ics1.tNS!!.process(ics1, iqData1, sf, false)
        if (ics2.isTNSDataPresent) ics2.tNS!!.process(ics2, iqData2, sf, false)

        //dependent coupling
        processDependentCoupling(true, elementID, CCE.AFTER_TNS, iqData1, iqData2)

        //filterbank
        filterBank.process(
            info1.windowSequence,
            info1.getWindowShape(ICSInfo.CURRENT),
            info1.getWindowShape(ICSInfo.PREVIOUS),
            iqData1,
            data!![channel],
            channel
        )
        filterBank.process(
            info2.windowSequence,
            info2.getWindowShape(ICSInfo.CURRENT),
            info2.getWindowShape(ICSInfo.PREVIOUS),
            iqData2,
            data!![channel + 1],
            channel + 1
        )
        if (LTPrediction.isLTPProfile(profile)) {
            ltp1!!  .updateState(data!![channel], filterBank.getOverlap(channel), profile)
            ltp2.updateState(data!![channel + 1], filterBank.getOverlap(channel + 1), profile)
        }

        //independent coupling
        processIndependentCoupling(true, elementID, data!![channel], data!![channel + 1])

        //gain control
        if (ics1.isGainControlPresent) ics1.getGainControl()!!.process(
            iqData1,
            info1.getWindowShape(ICSInfo.CURRENT),
            info1.getWindowShape(ICSInfo.PREVIOUS),
            info1.windowSequence
        )
        if (ics2.isGainControlPresent) ics2.getGainControl()!!.process(
            iqData2,
            info2.getWindowShape(ICSInfo.CURRENT),
            info2.getWindowShape(ICSInfo.PREVIOUS),
            info2.windowSequence
        )

        //SBR
        if (sbrPresent && config.isSBREnabled) {
            cpe.sBR!!.process(data!![channel], data!![channel + 1], false)
        }
    }

    private fun processIndependentCoupling(
        channelPair: Boolean,
        elementID: Int,
        data1: FloatArray,
        data2: FloatArray?
    ) {
        var index: Int
        var c: Int
        var chSelect: Int
        var cce: CCE?
        for (i in cces.indices) {
            cce = cces[i]
            index = 0
            if (cce != null && cce.couplingPoint == CCE.AFTER_IMDCT) {
                c = 0
                while (c <= cce.coupledCount) {
                    chSelect = cce.getCHSelect(c)
                    if (cce.isChannelPair(c) == channelPair && cce.getIDSelect(c) == elementID) {
                        if (chSelect != 1) {
                            cce.applyIndependentCoupling(index, data1)
                            if (chSelect != 0) index++
                        }
                        if (chSelect != 2) {
                            cce.applyIndependentCoupling(index, data2!!)
                            index++
                        }
                    } else index += 1 + if (chSelect == 3) 1 else 0
                    c++
                }
            }
        }
    }

    private fun processDependentCoupling(
        channelPair: Boolean,
        elementID: Int,
        couplingPoint: Int,
        data1: FloatArray,
        data2: FloatArray?
    ) {
        var index: Int
        var c: Int
        var chSelect: Int
        var cce: CCE?
        for (i in cces.indices) {
            cce = cces[i]
            index = 0
            if (cce != null && cce.couplingPoint == couplingPoint) {
                c = 0
                while (c <= cce.coupledCount) {
                    chSelect = cce.getCHSelect(c)
                    if (cce.isChannelPair(c) == channelPair && cce.getIDSelect(c) == elementID) {
                        if (chSelect != 1) {
                            cce.applyDependentCoupling(index, data1)
                            if (chSelect != 0) index++
                        }
                        if (chSelect != 2) {
                            cce.applyDependentCoupling(index, data2!!)
                            index++
                        }
                    } else index += 1 + if (chSelect == 3) 1 else 0
                    c++
                }
            }
        }
    }

    fun sendToOutput(buffer: SampleBuffer) {
        val be: Boolean = buffer.isBigEndian()
        val chs = data!!.size
        val mult = if (sbrPresent && config.isSBREnabled) 2 else 1
        val length: Int = mult * config.frameLength
        val freq: Int = mult * config.getSampleFrequency().frequency
        var b: ByteArray = buffer.data
        if (b.size != chs * length * 2) b = ByteArray(chs * length * 2)
        var cur: FloatArray
        var j: Int
        var off: Int
        var s: Short
        var i: Int = 0
        while (i < chs) {
            cur = data!![i]
            j = 0
            while (j < length) {
                s = max(min(cur[j].roundToInt(), Short.MAX_VALUE.toInt()), Short.MIN_VALUE.toInt()).toShort()
                off = (j * chs + i) * 2
                if (be) {
                    b[off] = (s.toInt() shr 8 and Constants.BYTE_MASK).toByte()
                    b[off + 1] = (s.toInt() and Constants.BYTE_MASK).toByte()
                } else {
                    b[off + 1] = (s.toInt() shr 8 and Constants.BYTE_MASK).toByte()
                    b[off] = (s.toInt() and Constants.BYTE_MASK).toByte()
                }
                j++
            }
            i++
        }
        buffer.setData(b, freq, chs, 16, bitsRead)
    }
}
