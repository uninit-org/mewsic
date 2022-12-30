package net.sourceforge.jaad.aac.filterbank
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.filterbank.MDCTTables.Companion.MDCT_TABLE_128
import net.sourceforge.jaad.aac.filterbank.MDCTTables.Companion.MDCT_TABLE_1920
import net.sourceforge.jaad.aac.filterbank.MDCTTables.Companion.MDCT_TABLE_2048
import net.sourceforge.jaad.aac.filterbank.MDCTTables.Companion.MDCT_TABLE_240

internal class MDCT(private val N: Int) : MDCTTables {
    private val N2: Int = N shr 1
    private val N4: Int = N shr 2
    private val N8: Int = N shr 3
    private val sincos  = when (N) {
        2048 -> MDCT_TABLE_2048
        256 -> MDCT_TABLE_128
        1920 -> MDCT_TABLE_1920
        240 -> {
            MDCT_TABLE_240
        }

        else -> throw AACException("unsupported MDCT length: $N")
    }
    private val fft: FFT = FFT(N4)
    private val buf: Array<FloatArray> = Array(N4) { FloatArray(2) }
    private val tmp: FloatArray = FloatArray(2)

    fun process(`in`: FloatArray, inOff: Int, out: FloatArray, outOff: Int) {

        //pre-IFFT complex multiplication
        var k = 0
        while (k < N4) {
            buf[k][1] = `in`[inOff + 2 * k] * sincos[k][0] + `in`[inOff + N2 - 1 - 2 * k] * sincos[k][1]
            buf[k][0] = `in`[inOff + N2 - 1 - 2 * k] * sincos[k][0] - `in`[inOff + 2 * k] * sincos[k][1]
            k++
        }

        //complex IFFT, non-scaling
        fft.process(buf, false)

        //post-IFFT complex multiplication
        k = 0
        while (k < N4) {
            tmp[0] = buf[k][0]
            tmp[1] = buf[k][1]
            buf[k][1] = tmp[1] * sincos[k][0] + tmp[0] * sincos[k][1]
            buf[k][0] = tmp[0] * sincos[k][0] - tmp[1] * sincos[k][1]
            k++
        }

        //reordering
        k = 0
        while (k < N8) {
            out[outOff + 2 * k] = buf[N8 + k][1]
            out[outOff + 2 + 2 * k] = buf[N8 + 1 + k][1]
            out[outOff + 1 + 2 * k] = -buf[N8 - 1 - k][0]
            out[outOff + 3 + 2 * k] = -buf[N8 - 2 - k][0]
            out[outOff + N4 + 2 * k] = buf[k][0]
            out[outOff + N4 + 2 + 2 * k] = buf[1 + k][0]
            out[outOff + N4 + 1 + 2 * k] = -buf[N4 - 1 - k][1]
            out[outOff + N4 + 3 + 2 * k] = -buf[N4 - 2 - k][1]
            out[outOff + N2 + 2 * k] = buf[N8 + k][0]
            out[outOff + N2 + 2 + 2 * k] = buf[N8 + 1 + k][0]
            out[outOff + N2 + 1 + 2 * k] = -buf[N8 - 1 - k][1]
            out[outOff + N2 + 3 + 2 * k] = -buf[N8 - 2 - k][1]
            out[outOff + N2 + N4 + 2 * k] = -buf[k][1]
            out[outOff + N2 + N4 + 2 + 2 * k] = -buf[1 + k][1]
            out[outOff + N2 + N4 + 1 + 2 * k] = buf[N4 - 1 - k][0]
            out[outOff + N2 + N4 + 3 + 2 * k] = buf[N4 - 2 - k][0]
            k += 2
        }
    }

    fun processForward(`in`: FloatArray, out: FloatArray) {
        var n: Int
        var k = 0
        //pre-FFT complex multiplication
        while (k < N8) {
            n = k shl 1
            tmp[0] = `in`[N - N4 - 1 - n] + `in`[N - N4 + n]
            tmp[1] = `in`[N4 + n] - `in`[N4 - 1 - n]
            buf[k][0] = tmp[0] * sincos[k][0] + tmp[1] * sincos[k][1]
            buf[k][1] = tmp[1] * sincos[k][0] - tmp[0] * sincos[k][1]
            buf[k][0] *= N.toFloat()
            buf[k][1] *= N.toFloat()
            tmp[0] = `in`[N2 - 1 - n] - `in`[n]
            tmp[1] = `in`[N2 + n] + `in`[N - 1 - n]
            buf[k + N8][0] = tmp[0] * sincos[k + N8][0] + tmp[1] * sincos[k + N8][1]
            buf[k + N8][1] = tmp[1] * sincos[k + N8][0] - tmp[0] * sincos[k + N8][1]
            buf[k + N8][0] *= N.toFloat()
            buf[k + N8][1] *= N.toFloat()
            k++
        }

        //complex FFT, non-scaling
        fft.process(buf, true)

        //post-FFT complex multiplication
        k = 0
        while (k < N4) {
            n = k shl 1
            tmp[0] = buf[k][0] * sincos[k][0] + buf[k][1] * sincos[k][1]
            tmp[1] = buf[k][1] * sincos[k][0] - buf[k][0] * sincos[k][1]
            out[n] = -tmp[0]
            out[N2 - 1 - n] = tmp[1]
            out[N2 + n] = -tmp[1]
            out[N - 1 - n] = tmp[0]
            k++
        }
    }
}
