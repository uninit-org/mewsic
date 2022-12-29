package net.sourceforge.jaad.aac.gain

import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.gain.GCConstants.Companion.BANDS
import net.sourceforge.jaad.aac.gain.GCConstants.Companion.ID_GAIN
import net.sourceforge.jaad.aac.gain.GCConstants.Companion.LN_GAIN
import net.sourceforge.jaad.aac.syntax.BitStream
import net.sourceforge.jaad.aac.syntax.ICSInfo.WindowSequence
import kotlin.math.log2
import kotlin.math.pow

class GainControl(private val frameLen: Int) : net.sourceforge.jaad.aac.gain.GCConstants {
    private val lbLong: Int
    private val lbShort: Int
    private val imdct: net.sourceforge.jaad.aac.gain.IMDCT
    private val ipqf: net.sourceforge.jaad.aac.gain.IPQF
    private val buffer1: FloatArray
    private val function: FloatArray
    private val buffer2: Array<FloatArray>
    private val overlap: Array<FloatArray>
    private var maxBand = 0
    private lateinit var level: Array<Array<IntArray?>>
    private val levelPrev: Array<Array<IntArray>?>
    private lateinit var location: Array<Array<IntArray?>>
    private val locationPrev: Array<Array<IntArray>?>

    init {
        lbLong = frameLen / BANDS
        lbShort = lbLong / 8
        imdct = net.sourceforge.jaad.aac.gain.IMDCT(frameLen)
        ipqf = net.sourceforge.jaad.aac.gain.IPQF()
        levelPrev = arrayOfNulls(0)
        locationPrev = arrayOfNulls(0)
        buffer1 = FloatArray(frameLen / 2)
        buffer2 = Array(BANDS) { FloatArray(lbLong) }
        function = FloatArray(lbLong * 2)
        overlap = Array(BANDS) { FloatArray(lbLong * 2) }
    }

    @Throws(AACException::class)
    fun decode(`in`: BitStream, winSeq: WindowSequence?) {
        maxBand = `in`.readBits(2) + 1
        val wdLen: Int
        val locBits: Int
        var locBits2 = 0
        when (winSeq) {
            WindowSequence.ONLY_LONG_SEQUENCE -> {
                wdLen = 1
                locBits = 5
                locBits2 = 5
            }

            WindowSequence.EIGHT_SHORT_SEQUENCE -> {
                wdLen = 8
                locBits = 2
                locBits2 = 2
            }

            WindowSequence.LONG_START_SEQUENCE -> {
                wdLen = 2
                locBits = 4
                locBits2 = 2
            }

            WindowSequence.LONG_STOP_SEQUENCE -> {
                wdLen = 2
                locBits = 4
                locBits2 = 5
            }

            else -> return
        }
        level = Array(maxBand) { arrayOfNulls(wdLen) }
        location = Array(maxBand) { arrayOfNulls(wdLen) }
        var wd: Int
        var k: Int
        var len: Int
        var bits: Int
        for (bd in 1 until maxBand) {
            wd = 0
            while (wd < wdLen) {
                len = `in`.readBits(3)
                level[bd][wd] = IntArray(len)
                location[bd][wd] = IntArray(len)
                k = 0
                while (k < len) {
                    level[bd][wd]!![k] = `in`.readBits(4)
                    bits = if (wd == 0) locBits else locBits2
                    location[bd][wd]!![k] = `in`.readBits(bits)
                    k++
                }
                wd++
            }
        }
    }

    @Throws(AACException::class)
    fun process(data: FloatArray, winShape: Int, winShapePrev: Int, winSeq: WindowSequence) {
        imdct.process(data, buffer1, winShape, winShapePrev, winSeq)
        for (i in 0 until BANDS) {
            compensate(buffer1, buffer2, winSeq, i)
        }
        ipqf.process(buffer2, frameLen, maxBand, data)
    }

    /**
     * gain compensation and overlap-add:
     * - the gain control function is calculated
     * - the gain control function applies to IMDCT output samples as a another IMDCT window
     * - the reconstructed time domain signal produces by overlap-add
     */
    private fun compensate(`in`: FloatArray, out: Array<FloatArray>, winSeq: WindowSequence, band: Int) {
        var j: Int
        if (winSeq == WindowSequence.EIGHT_SHORT_SEQUENCE) {
            var a: Int
            var b: Int
            for (k in 0..7) {
                //calculation
                calculateFunctionData(lbShort * 2, band, winSeq, k)
                //applying
                j = 0
                while (j < lbShort * 2) {
                    a = band * lbLong * 2 + k * lbShort * 2 + j
                    `in`[a] *= function[j]
                    j++
                }
                //overlapping
                j = 0
                while (j < lbShort) {
                    a = j + lbLong * 7 / 16 + lbShort * k
                    b = band * lbLong * 2 + k * lbShort * 2 + j
                    overlap[band][a] += `in`[b]
                    j++
                }
                //store for next frame
                j = 0
                while (j < lbShort) {
                    a = j + lbLong * 7 / 16 + lbShort * (k + 1)
                    b = band * lbLong * 2 + k * lbShort * 2 + lbShort + j
                    overlap[band][a] = `in`[b]
                    j++
                }
                locationPrev[band]!![0] = location[band][k]!!.copyOf()
                levelPrev[band]!![0] = level[band][k]!!.copyOf()
            }
            overlap[band].copyInto(out[band], 0, 0, lbLong)
            overlap[band].copyInto(overlap[band], lbLong, 0, lbLong)
        } else {
            //calculation
            calculateFunctionData(lbLong * 2, band, winSeq, 0)
            //applying
            j = 0
            while (j < lbLong * 2) {
                `in`[band * lbLong * 2 + j] *= function[j]
                j++
            }
            //overlapping
            j = 0
            while (j < lbLong) {
                out[band][j] = overlap[band][j] + `in`[band * lbLong * 2 + j]
                j++
            }
            //store for next frame
            j = 0
            while (j < lbLong) {
                overlap[band][j] = `in`[band * lbLong * 2 + lbLong + j]
                j++
            }
            val lastBlock = if (winSeq == WindowSequence.ONLY_LONG_SEQUENCE) 1 else 0
            locationPrev[band]!![0] = location[band][lastBlock]!!.copyOf()
            levelPrev[band]!![0] = level[band][lastBlock]!!.copyOf()
        }
    }

    //produces gain control function data, stores it in 'function' array
    private fun calculateFunctionData(samples: Int, band: Int, winSeq: WindowSequence, blockID: Int) {
        val locA = IntArray(10)
        val levA = FloatArray(10)
        val modFunc = FloatArray(samples)
        val buf1 = FloatArray(samples / 2)
        val buf2 = FloatArray(samples / 2)
        val buf3 = FloatArray(samples / 2)
        var maxLocGain0 = 0
        var maxLocGain1 = 0
        var maxLocGain2 = 0
        when (winSeq) {
            WindowSequence.ONLY_LONG_SEQUENCE, WindowSequence.EIGHT_SHORT_SEQUENCE -> {
                run {
                    maxLocGain1 = samples / 2
                    maxLocGain0 = maxLocGain1
                }
                maxLocGain2 = 0
            }

            WindowSequence.LONG_START_SEQUENCE -> {
                maxLocGain0 = samples / 2
                maxLocGain1 = samples * 7 / 32
                maxLocGain2 = samples / 16
            }

            WindowSequence.LONG_STOP_SEQUENCE -> {
                maxLocGain0 = samples / 16
                maxLocGain1 = samples * 7 / 32
                maxLocGain2 = samples / 2
            }
        }

        //calculate the fragment modification functions
        //for the first half region
        calculateFMD(band, 0, true, maxLocGain0, samples, locA, levA, buf1)

        //for the latter half region
        val block = if (winSeq == WindowSequence.EIGHT_SHORT_SEQUENCE) blockID else 0
        val secLevel = calculateFMD(band, block, false, maxLocGain1, samples, locA, levA, buf2)

        //for the non-overlapped region
        if (winSeq == WindowSequence.LONG_START_SEQUENCE || winSeq == WindowSequence.LONG_STOP_SEQUENCE) {
            calculateFMD(band, 1, false, maxLocGain2, samples, locA, levA, buf3)
        }

        //calculate a gain modification function
        var i: Int
        var flatLen = 0
        if (winSeq == WindowSequence.LONG_STOP_SEQUENCE) {
            flatLen = samples / 2 - maxLocGain0 - maxLocGain1
            i = 0
            while (i < flatLen) {
                modFunc[i] = 1.0f
                i++
            }
        }
        if (winSeq == WindowSequence.ONLY_LONG_SEQUENCE || winSeq == WindowSequence.EIGHT_SHORT_SEQUENCE) levA[0] = 1.0f
        i = 0
        while (i < maxLocGain0) {
            modFunc[i + flatLen] = levA[0] * secLevel * buf1[i]
            i++
        }
        i = 0
        while (i < maxLocGain1) {
            modFunc[i + flatLen + maxLocGain0] = levA[0] * buf2[i]
            i++
        }
        if (winSeq == WindowSequence.LONG_START_SEQUENCE) {
            i = 0
            while (i < maxLocGain2) {
                modFunc[i + maxLocGain0 + maxLocGain1] = buf3[i]
                i++
            }
            flatLen = samples / 2 - maxLocGain1 - maxLocGain2
            i = 0
            while (i < flatLen) {
                modFunc[i + maxLocGain0 + maxLocGain1 + maxLocGain2] = 1.0f
                i++
            }
        } else if (winSeq == WindowSequence.LONG_STOP_SEQUENCE) {
            i = 0
            while (i < maxLocGain2) {
                modFunc[i + flatLen + maxLocGain0 + maxLocGain1] = buf3[i]
                i++
            }
        }

        //calculate a gain control function
        i = 0
        while (i < samples) {
            function[i] = 1.0f / modFunc[i]
            i++
        }
    }

    /*
	 * calculates a fragment modification function by interpolating the gain
	 * values of the gain change positions
	 */
    private fun calculateFMD(
        bd: Int, wd: Int, prev: Boolean, maxLocGain: Int, samples: Int,
        loc: IntArray, lev: FloatArray, fmd: FloatArray
    ): Float {
        val m = IntArray(samples / 2)
        val lct = if (prev) locationPrev[bd]!![wd] else location[bd][wd]!!
        val lvl = if (prev) levelPrev[bd]!![wd] else level[bd][wd]!!
        val length = lct.size
        var lngain: Int
        var i: Int
        i = 0
        while (i < length) {
            loc[i + 1] = 8 * lct[i] //gainc
            lngain = getGainChangePointID(lvl[i]) //gainc
            if (lngain < 0) lev[i + 1] = 1.0f / 2.0.pow(-lngain.toDouble()).toFloat() else lev[i + 1] =
                2.0.pow(lngain.toDouble()).toFloat()
            i++
        }

        //set start point values
        loc[0] = 0
        if (length == 0) lev[0] = 1.0f else lev[0] = lev[1]
        val secLevel = lev[0]

        //set end point values
        loc[length + 1] = maxLocGain
        lev[length + 1] = 1.0f
        var j: Int
        i = 0
        while (i < maxLocGain) {
            m[i] = 0
            j = 0
            while (j <= length + 1) {
                if (loc[j] <= i) m[i] = j
                j++
            }
            i++
        }
        i = 0
        while (i < maxLocGain) {
            if (i >= loc[m[i]] && i <= loc[m[i]] + 7) fmd[i] =
                interpolateGain(lev[m[i]], lev[m[i] + 1], i - loc[m[i]]) else fmd[i] = lev[m[i] + 1]
            i++
        }
        return secLevel
    }

    /**
     * transformes the exponent value of the gain to the id of the gain change
     * point
     */
    private fun getGainChangePointID(lngain: Int): Int {
        for (i in 0 until ID_GAIN) {
            if (lngain == LN_GAIN.get(i)) return i
        }
        return 0 //shouldn't happen
    }

    /**
     * calculates a fragment modification function
     * the interpolated gain value between the gain values of two gain change
     * positions is calculated by the formula:
     * f(a,b,j) = 2^(((8-j)log2(a)+j*log2(b))/8)
     */
    private fun interpolateGain(alev0: Float, alev1: Float, iloc: Int): Float {
        val a0: Float = log2(alev0.toDouble()).toFloat()
        val a1: Float = log2(alev1.toDouble()).toFloat()
        return 2.0.pow((((8 - iloc) * a0 + iloc * a1) / 8).toDouble()).toFloat()
    }
}
