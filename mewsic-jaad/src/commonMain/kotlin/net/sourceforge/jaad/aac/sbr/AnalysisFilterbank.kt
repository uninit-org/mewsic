package net.sourceforge.jaad.aac.sbr

import net.sourceforge.jaad.aac.sbr.FilterbankTable.Companion.qmf_c;
class AnalysisFilterbank(private val channels: Int) : net.sourceforge.jaad.aac.sbr.FilterbankTable {
    private val x //x is implemented as double ringbuffer
            : FloatArray
    private var x_index //ringbuffer index
            = 0

    init {
        x = FloatArray(2 * channels * 10)
    }

    fun reset() {
        // kotlin
        x.fill(0f)
    }

    fun sbr_qmf_analysis_32(
        sbr: net.sourceforge.jaad.aac.sbr.SBR, input: FloatArray,
        X: Array<Array<FloatArray>>, offset: Int, kx: Int
    ) {
        val u = FloatArray(64)
        val in_real = FloatArray(32)
        val in_imag = FloatArray(32)
        val out_real = FloatArray(32)
        val out_imag = FloatArray(32)
        var `in` = 0
        var l: Int

        /* qmf subsample l */l = 0
        while (l < sbr.numTimeSlotsRate) {
            var n: Int

            /* shift input buffer x */
            /* input buffer is not shifted anymore, x is implemented as double ringbuffer */
            //memmove(qmfa.x + 32, qmfa.x, (320-32)*sizeof(real_t));

            /* add new samples to input buffer x */n = 32 - 1
            while (n >= 0) {
                x[x_index + n + 320] = input[`in`++]
                x[x_index + n] = x[x_index + n + 320]
                n--
            }

            /* window and summation to create array u */n = 0
            while (n < 64) {
                u[n] =
                    x[x_index + n] * qmf_c.get(2 * n) + x[x_index + n + 64] * qmf_c.get(2 * (n + 64)) + x[x_index + n + 128] * qmf_c.get(
                        2 * (n + 128)
                    ) + x[x_index + n + 192] * qmf_c.get(2 * (n + 192)) + x[x_index + n + 256] * qmf_c.get(2 * (n + 256))
                n++
            }

            /* update ringbuffer index */x_index -= 32
            if (x_index < 0) x_index = 320 - 32

            /* calculate 32 subband samples by introducing X */
            // Reordering of data moved from DCT_IV to here
            in_imag[31] = u[1]
            in_real[0] = u[0]
            n = 1
            while (n < 31) {
                in_imag[31 - n] = u[n + 1]
                in_real[n] = -u[64 - n]
                n++
            }
            in_imag[0] = u[32]
            in_real[31] = -u[33]

            // dct4_kernel is DCT_IV without reordering which is done before and after FFT
            net.sourceforge.jaad.aac.sbr.DCT.dct4_kernel(in_real, in_imag, out_real, out_imag)

            // Reordering of data moved from DCT_IV to here
            n = 0
            while (n < 16) {
                if (2 * n + 1 < kx) {
                    X[l + offset][2 * n][0] = 2.0f * out_real[n]
                    X[l + offset][2 * n][1] = 2.0f * out_imag[n]
                    X[l + offset][2 * n + 1][0] = -2.0f * out_imag[31 - n]
                    X[l + offset][2 * n + 1][1] = -2.0f * out_real[31 - n]
                } else {
                    if (2 * n < kx) {
                        X[l + offset][2 * n][0] = 2.0f * out_real[n]
                        X[l + offset][2 * n][1] = 2.0f * out_imag[n]
                    } else {
                        X[l + offset][2 * n][0] = 0f
                        X[l + offset][2 * n][1] = 0f
                    }
                    X[l + offset][2 * n + 1][0] = 0f
                    X[l + offset][2 * n + 1][1] = 0f
                }
                n++
            }
            l++
        }
    }
}
