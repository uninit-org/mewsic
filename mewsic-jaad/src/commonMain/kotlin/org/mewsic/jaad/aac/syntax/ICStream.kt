package net.sourceforge.jaad.aac.syntax

import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.ChannelConfiguration
import net.sourceforge.jaad.aac.DecoderConfig
import net.sourceforge.jaad.aac.error.RVLC
import net.sourceforge.jaad.aac.gain.GainControl
import net.sourceforge.jaad.aac.huffman.HCB
import net.sourceforge.jaad.aac.huffman.HCB.Companion.FIRST_PAIR_HCB
import net.sourceforge.jaad.aac.huffman.HCB.Companion.INTENSITY_HCB
import net.sourceforge.jaad.aac.huffman.HCB.Companion.INTENSITY_HCB2
import net.sourceforge.jaad.aac.huffman.HCB.Companion.NOISE_HCB
import net.sourceforge.jaad.aac.huffman.HCB.Companion.ZERO_HCB
import net.sourceforge.jaad.aac.huffman.Huffman
import net.sourceforge.jaad.aac.syntax.IQTable.Companion.IQ_TABLE
import net.sourceforge.jaad.aac.syntax.ScaleFactorTable.Companion.SCALEFACTOR_TABLE
import net.sourceforge.jaad.aac.tools.TNS
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

//TODO: apply pulse data
class ICStream(private val frameLength: Int) : Constants, HCB, ScaleFactorTable,
    IQTable {
    //always needed
    val info: ICSInfo
    val sfbCB: IntArray
    val sectEnd: IntArray

    /**
     * Does inverse quantization and applies the scale factors on the decoded
     * data. After this the noiseless decoding is finished and the decoded data
     * is returned.
     * @return the inverse quantized and scaled data
     */
    @get:Throws(AACException::class)
    val invQuantData: FloatArray
    val scaleFactors: FloatArray
    var globalGain = 0
        private set
    private var pulseDataPresent = false
    var isTNSDataPresent = false
        private set
    var isGainControlPresent = false
        private set

    //only allocated if needed
    private var tns: TNS? = null
    private var gainControl: GainControl? = null
    private var pulseOffset: IntArray? = null
    private var pulseAmp: IntArray = IntArray(0)
    private var pulseCount = 0
    private var pulseStartSWB = 0

    //error resilience
    val isNoiseUsed = false
    var reorderedSpectralDataLength = 0
        private set
    var longestCodewordLength = 0
        private set
    private var rvlc: RVLC? = null

    init {
        info = ICSInfo(frameLength)
        sfbCB = IntArray(Constants.MAX_SECTIONS)
        sectEnd = IntArray(Constants.MAX_SECTIONS)
        invQuantData = FloatArray(frameLength)
        scaleFactors = FloatArray(Constants.MAX_SECTIONS)
    }

    /* ========= decoding ========== */
    @Throws(AACException::class)
    override fun decode(`in`: BitStream, commonWindow: Boolean, conf: DecoderConfig) {
        if (conf.isScalefactorResilienceUsed && rvlc == null) rvlc = RVLC()
        val er: Boolean = conf.getProfile()!!.isErrorResilientProfile
        globalGain = `in`.readBits(8)
        if (!commonWindow) info.decode(`in`, conf, commonWindow)
        decodeSectionData(`in`, conf.isSectionDataResilienceUsed)

        //if(conf.isScalefactorResilienceUsed()) rvlc.decode(in, this, scaleFactors);
        /*else*/decodeScaleFactors(`in`)
        pulseDataPresent = `in`.readBool()
        if (pulseDataPresent) {
            if (info.isEightShortFrame) throw AACException("pulse data not allowed for short frames")
            decodePulseData(`in`)
        }
        isTNSDataPresent = `in`.readBool()
        if (isTNSDataPresent && !er) {
            if (tns == null) tns = TNS()
            tns!!.decode(`in`, info)
        }
        isGainControlPresent = `in`.readBool()
        if (isGainControlPresent) {
            if (gainControl == null) gainControl = GainControl(frameLength)
            gainControl!!.decode(`in`, info.windowSequence)
        }

        //RVLC spectral data
        //if(conf.isScalefactorResilienceUsed()) rvlc.decodeScalefactors(this, in, scaleFactors);
        if (conf.isSpectralDataResilienceUsed) {
            val max = if (conf.getChannelConfiguration() === ChannelConfiguration.CHANNEL_CONFIG_STEREO) 6144 else 12288
            reorderedSpectralDataLength = max(`in`.readBits(14), max)
            longestCodewordLength = max(`in`.readBits(6), 49)
            //HCR.decodeReorderedSpectralData(this, in, data, conf.isSectionDataResilienceUsed());
        } else decodeSpectralData(`in`)
    }

    @Throws(AACException::class)
    fun decodeSectionData(`in`: BitStream, sectionDataResilienceUsed: Boolean) {
        sfbCB.fill(0)
        sectEnd.fill(0)
        val bits = if (info.isEightShortFrame) 3 else 5
        val escVal = (1 shl bits) - 1
        val windowGroupCount = info.windowGroupCount
        val maxSFB = info.maxSFB
        var end: Int
        var cb: Int
        var incr: Int
        var idx = 0
        for (g in 0 until windowGroupCount) {
            var k = 0
            while (k < maxSFB) {
                end = k
                cb = `in`.readBits(4)
                if (cb == 12) throw AACException("invalid huffman codebook: 12")
                while (`in`.readBits(bits).also { incr = it } == escVal) {
                    end += incr
                }
                end += incr
                if (end > maxSFB) throw AACException("too many bands: $end, allowed: $maxSFB")
                while (k < end) {
                    sfbCB[idx] = cb
                    sectEnd[idx++] = end
                    k++
                }
            }
        }
    }

    @Throws(AACException::class)
    private fun decodePulseData(`in`: BitStream) {
        pulseCount = `in`.readBits(2) + 1
        pulseStartSWB = `in`.readBits(6)
        if (pulseStartSWB >= info.sWBCount) throw AACException("pulse SWB out of range: " + pulseStartSWB + " > " + info.sWBCount)
        if (pulseOffset == null || pulseCount != pulseOffset!!.size) {
            //only reallocate if needed
            pulseOffset = IntArray(pulseCount)
            pulseAmp = IntArray(pulseCount)
        }
        pulseOffset!![0] = info.sWBOffsets[pulseStartSWB]
        pulseOffset!![0] += `in`.readBits(5)
        pulseAmp[0] = `in`.readBits(4)
        for (i in 1 until pulseCount) {
            pulseOffset!![i] = `in`.readBits(5) + pulseOffset!![i - 1]
            if (pulseOffset!![i] > 1023) throw AACException("pulse offset out of range: " + pulseOffset!![0])
            pulseAmp[i] = `in`.readBits(4)
        }
    }

    @Throws(AACException::class)
    fun decodeScaleFactors(`in`: BitStream) {
        val windowGroups = info.windowGroupCount
        val maxSFB = info.maxSFB
        //0: spectrum, 1: noise, 2: intensity
        val offset = intArrayOf(globalGain, globalGain - 90, 0)
        var tmp: Int
        var noiseFlag = true
        var sfb: Int
        var idx = 0
        for (g in 0 until windowGroups) {
            sfb = 0
            while (sfb < maxSFB) {
                val end = sectEnd[idx]
                when (sfbCB[idx]) {
                    ZERO_HCB -> while (sfb < end) {
                        scaleFactors[idx] = 0f
                        sfb++
                        idx++
                    }

                    INTENSITY_HCB, INTENSITY_HCB2 -> while (sfb < end) {
                        offset[2] += Huffman.decodeScaleFactor(`in`) - SF_DELTA
                        tmp = min(max(offset[2], -155), 100)
                        scaleFactors[idx] = SCALEFACTOR_TABLE.get(-tmp + SF_OFFSET)
                        sfb++
                        idx++
                    }

                    NOISE_HCB -> while (sfb < end) {
                        if (noiseFlag) {
                            offset[1] += `in`.readBits(9) - 256
                            noiseFlag = false
                        } else offset[1] += Huffman.decodeScaleFactor(`in`) - SF_DELTA
                        tmp = min(max(offset[1], -100), 155)
                        scaleFactors[idx] = -SCALEFACTOR_TABLE.get(tmp + SF_OFFSET)
                        sfb++
                        idx++
                    }

                    else -> while (sfb < end) {
                        offset[0] += Huffman.decodeScaleFactor(`in`) - SF_DELTA
                        if (offset[0] > 255) throw AACException("scalefactor out of range: " + offset[0])
                        scaleFactors[idx] = SCALEFACTOR_TABLE.get(offset[0] - 100 + SF_OFFSET)
                        sfb++
                        idx++
                    }
                }
            }
        }
    }

    @Throws(AACException::class)
    private fun decodeSpectralData(`in`: BitStream) {
        invQuantData.fill(0f)
        val maxSFB = info.maxSFB
        val windowGroups = info.windowGroupCount
        val offsets = info.sWBOffsets
        val buf = IntArray(4)
        var sfb: Int
        var j: Int
        var k: Int
        var w: Int
        var hcb: Int
        var off: Int
        var width: Int
        var num: Int
        var groupOff = 0
        var idx = 0
        for (g in 0 until windowGroups) {
            val groupLen = info.getWindowGroupLength(g)
            sfb = 0
            while (sfb < maxSFB) {
                hcb = sfbCB[idx]
                off = groupOff + offsets[sfb]
                width = offsets[sfb + 1] - offsets[sfb]
                if (hcb == ZERO_HCB || hcb == INTENSITY_HCB || hcb == INTENSITY_HCB2) {
                    w = 0
                    while (w < groupLen) {
                        invQuantData.fill(off.toFloat(), off + width, 0)
                        w++
                        off += 128
                    }
                } else if (hcb == NOISE_HCB) {
                    //apply PNS: fill with random values
                    w = 0
                    while (w < groupLen) {
                        var energy = 0f
                        k = 0
                        while (k < width) {
                            randomState *= 1664525 + 1013904223
                            invQuantData[off + k] = randomState.toFloat()
                            energy += invQuantData[off + k] * invQuantData[off + k]
                            k++
                        }
                        val scale: Float = (scaleFactors[idx] / sqrt(energy.toDouble())).toFloat()
                        k = 0
                        while (k < width) {
                            invQuantData[off + k] *= scale
                            k++
                        }
                        w++
                        off += 128
                    }
                } else {
                    w = 0
                    while (w < groupLen) {
                        num = if (hcb >= FIRST_PAIR_HCB) 2 else 4
                        k = 0
                        while (k < width) {
                            Huffman.decodeSpectralData(`in`, hcb, buf, 0)

                            //inverse quantization & scaling
                            j = 0
                            while (j < num) {
                                invQuantData[off + k + j] =
                                    if (buf[j] > 0) IQ_TABLE.get(buf[j]) else -IQ_TABLE.get(-buf[j])
                                invQuantData[off + k + j] *= scaleFactors[idx]
                                j++
                            }
                            k += num
                        }
                        w++
                        off += 128
                    }
                }
                sfb++
                idx++
            }
            groupOff += groupLen shl 7
        }
    }

    /* =========== gets ============ */
    val tNS: TNS?
        get() = tns

    fun getGainControl(): GainControl? {
        return gainControl
    }

    companion object {
        private const val SF_DELTA = 60
        private const val SF_OFFSET = 200
        private var randomState = 0x1F2E3D4C
    }
}
