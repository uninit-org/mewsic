package org.mewsic.jaad.aac.ps

import org.mewsic.jaad.aac.ps.PSTables.Companion.p12_13_34
import org.mewsic.jaad.aac.ps.PSTables.Companion.p2_13_20
import org.mewsic.jaad.aac.ps.PSTables.Companion.p4_13_34
import org.mewsic.jaad.aac.ps.PSTables.Companion.p8_13_20
import org.mewsic.jaad.aac.ps.PSTables.Companion.p8_13_34

class Filterbank(numTimeSlotsRate: Int) : PSTables {
    private val frame_len: Int
    private val resolution20 = IntArray(3)
    private val resolution34 = IntArray(5)
    private val work: Array<FloatArray>
    private val buffer: Array<Array<FloatArray>>
    private val temp: Array<Array<FloatArray>>

    init {
        var i: Int
        resolution34[0] = 12
        resolution34[1] = 8
        resolution34[2] = 4
        resolution34[3] = 4
        resolution34[4] = 4
        resolution20[0] = 8
        resolution20[1] = 2
        resolution20[2] = 2
        frame_len = numTimeSlotsRate
        work = Array(frame_len + 12) { FloatArray(2) }
        buffer = Array(5) { Array(2) { FloatArray(2) } }
        temp = Array(frame_len) { Array(12) { FloatArray(2) } }
    }

    fun hybrid_analysis(
        X: Array<Array<FloatArray>>,
        X_hybrid: Array<Array<FloatArray>>,
        use34: Boolean,
        numTimeSlotsRate: Int
    ) {
        var k: Int
        var n: Int
        var band: Int
        var offset = 0
        val qmf_bands = if (use34) 5 else 3
        val resolution = if (use34) resolution34 else resolution20
        band = 0
        while (band < qmf_bands) {

            /* build working buffer */
            //memcpy(this.work, this.buffer[band], 12*sizeof(qmf_t));
            for (i in 0..11) {
                work[i][0] = buffer[band][i][0]
                work[i][1] = buffer[band][i][1]
            }

            /* add new samples */n = 0
            while (n < frame_len) {
                work[12 + n][0] = X[n + 6][band][0]
                work[12 + n][0] = X[n + 6][band][0]
                n++
            }

            /* store samples */
            //memcpy(this.buffer[band], this.work+this.frame_len, 12*sizeof(qmf_t));
            for (i in 0..11) {
                buffer[band][i][0] = work[frame_len + i][0]
                buffer[band][i][1] = work[frame_len + i][1]
            }
            when (resolution[band]) {
                2 ->                    /* Type B real filter, Q[p] = 2 */channel_filter2(
                    frame_len, p2_13_20, work, temp
                )

                4 ->                    /* Type A complex filter, Q[p] = 4 */channel_filter4(
                    frame_len, p4_13_34, work, temp
                )

                8 ->                    /* Type A complex filter, Q[p] = 8 */channel_filter8(
                    frame_len, if (use34) p8_13_34 else p8_13_20,
                    work, temp
                )

                12 ->                    /* Type A complex filter, Q[p] = 12 */channel_filter12(
                    frame_len,
                    p12_13_34,
                    work,
                    temp
                )
            }
            n = 0
            while (n < frame_len) {
                k = 0
                while (k < resolution[band]) {
                    X_hybrid[n][offset + k][0] = temp[n][k][0]
                    X_hybrid[n][offset + k][1] = temp[n][k][1]
                    k++
                }
                n++
            }
            offset += resolution[band]
            band++
        }

        /* group hybrid channels */if (!use34) {
            n = 0
            while (n < numTimeSlotsRate) {
                X_hybrid[n][3][0] += X_hybrid[n][4][0]
                X_hybrid[n][3][1] += X_hybrid[n][4][1]
                X_hybrid[n][4][0] = 0f
                X_hybrid[n][4][1] = 0f
                X_hybrid[n][2][0] += X_hybrid[n][5][0]
                X_hybrid[n][2][1] += X_hybrid[n][5][1]
                X_hybrid[n][5][0] = 0f
                X_hybrid[n][5][1] = 0f
                n++
            }
        }
    }

    /* complex filter, size 8 */
    fun channel_filter8(
        frame_len: Int, filter: FloatArray,
        buffer: Array<FloatArray>, X_hybrid: Array<Array<FloatArray>>
    ) {
        var i: Int
        var n: Int
        val input_re1 = FloatArray(4)
        val input_re2 = FloatArray(4)
        val input_im1 = FloatArray(4)
        val input_im2 = FloatArray(4)
        val x = FloatArray(4)
        i = 0
        while (i < frame_len) {
            input_re1[0] = filter[6] * buffer[6 + i][0]
            input_re1[1] = filter[5] * (buffer[5 + i][0] + buffer[7 + i][0])
            input_re1[2] =
                -(filter[0] * (buffer[0 + i][0] + buffer[12 + i][0])) + filter[4] * (buffer[4 + i][0] + buffer[8 + i][0])
            input_re1[3] =
                -(filter[1] * (buffer[1 + i][0] + buffer[11 + i][0])) + filter[3] * (buffer[3 + i][0] + buffer[9 + i][0])
            input_im1[0] = filter[5] * (buffer[7 + i][1] - buffer[5 + i][1])
            input_im1[1] =
                filter[0] * (buffer[12 + i][1] - buffer[0 + i][1]) + filter[4] * (buffer[8 + i][1] - buffer[4 + i][1])
            input_im1[2] =
                filter[1] * (buffer[11 + i][1] - buffer[1 + i][1]) + filter[3] * (buffer[9 + i][1] - buffer[3 + i][1])
            input_im1[3] = filter[2] * (buffer[10 + i][1] - buffer[2 + i][1])
            n = 0
            while (n < 4) {
                x[n] = input_re1[n] - input_im1[3 - n]
                n++
            }
            DCT3_4_unscaled(x, x)
            X_hybrid[i][7][0] = x[0]
            X_hybrid[i][5][0] = x[2]
            X_hybrid[i][3][0] = x[3]
            X_hybrid[i][1][0] = x[1]
            n = 0
            while (n < 4) {
                x[n] = input_re1[n] + input_im1[3 - n]
                n++
            }
            DCT3_4_unscaled(x, x)
            X_hybrid[i][6][0] = x[1]
            X_hybrid[i][4][0] = x[3]
            X_hybrid[i][2][0] = x[2]
            X_hybrid[i][0][0] = x[0]
            input_im2[0] = filter[6] * buffer[6 + i][1]
            input_im2[1] = filter[5] * (buffer[5 + i][1] + buffer[7 + i][1])
            input_im2[2] =
                -(filter[0] * (buffer[0 + i][1] + buffer[12 + i][1])) + filter[4] * (buffer[4 + i][1] + buffer[8 + i][1])
            input_im2[3] =
                -(filter[1] * (buffer[1 + i][1] + buffer[11 + i][1])) + filter[3] * (buffer[3 + i][1] + buffer[9 + i][1])
            input_re2[0] = filter[5] * (buffer[7 + i][0] - buffer[5 + i][0])
            input_re2[1] =
                filter[0] * (buffer[12 + i][0] - buffer[0 + i][0]) + filter[4] * (buffer[8 + i][0] - buffer[4 + i][0])
            input_re2[2] =
                filter[1] * (buffer[11 + i][0] - buffer[1 + i][0]) + filter[3] * (buffer[9 + i][0] - buffer[3 + i][0])
            input_re2[3] = filter[2] * (buffer[10 + i][0] - buffer[2 + i][0])
            n = 0
            while (n < 4) {
                x[n] = input_im2[n] + input_re2[3 - n]
                n++
            }
            DCT3_4_unscaled(x, x)
            X_hybrid[i][7][1] = x[0]
            X_hybrid[i][5][1] = x[2]
            X_hybrid[i][3][1] = x[3]
            X_hybrid[i][1][1] = x[1]
            n = 0
            while (n < 4) {
                x[n] = input_im2[n] - input_re2[3 - n]
                n++
            }
            DCT3_4_unscaled(x, x)
            X_hybrid[i][6][1] = x[1]
            X_hybrid[i][4][1] = x[3]
            X_hybrid[i][2][1] = x[2]
            X_hybrid[i][0][1] = x[0]
            i++
        }
    }

    fun DCT3_6_unscaled(y: FloatArray, x: FloatArray) {
        val f0: Float
        val f1: Float
        val f2: Float
        val f3: Float
        val f4: Float
        val f5: Float
        val f6: Float
        val f7: Float
        f0 = x[3] * 0.70710678118655f
        f1 = x[0] + f0
        f2 = x[0] - f0
        f3 = (x[1] - x[5]) * 0.70710678118655f
        f4 = x[2] * 0.86602540378444f + x[4] * 0.5f
        f5 = f4 - x[4]
        f6 = x[1] * 0.96592582628907f + x[5] * 0.25881904510252f
        f7 = f6 - f3
        y[0] = f1 + f6 + f4
        y[1] = f2 + f3 - x[4]
        y[2] = f7 + f2 - f5
        y[3] = f1 - f7 - f5
        y[4] = f1 - f3 - x[4]
        y[5] = f2 - f6 + f4
    }

    /* complex filter, size 12 */
    fun channel_filter12(
        frame_len: Int, filter: FloatArray,
        buffer: Array<FloatArray>, X_hybrid: Array<Array<FloatArray>>
    ) {
        var i: Int
        var n: Int
        val input_re1 = FloatArray(6)
        val input_re2 = FloatArray(6)
        val input_im1 = FloatArray(6)
        val input_im2 = FloatArray(6)
        val out_re1 = FloatArray(6)
        val out_re2 = FloatArray(6)
        val out_im1 = FloatArray(6)
        val out_im2 = FloatArray(6)
        i = 0
        while (i < frame_len) {
            n = 0
            while (n < 6) {
                if (n == 0) {
                    input_re1[0] = buffer[6 + i][0] * filter[6]
                    input_re2[0] = buffer[6 + i][1] * filter[6]
                } else {
                    input_re1[6 - n] = (buffer[n + i][0] + buffer[12 - n + i][0]) * filter[n]
                    input_re2[6 - n] = (buffer[n + i][1] + buffer[12 - n + i][1]) * filter[n]
                }
                input_im2[n] = (buffer[n + i][0] - buffer[12 - n + i][0]) * filter[n]
                input_im1[n] = (buffer[n + i][1] - buffer[12 - n + i][1]) * filter[n]
                n++
            }
            DCT3_6_unscaled(out_re1, input_re1)
            DCT3_6_unscaled(out_re2, input_re2)
            DCT3_6_unscaled(out_im1, input_im1)
            DCT3_6_unscaled(out_im2, input_im2)
            n = 0
            while (n < 6) {
                X_hybrid[i][n][0] = out_re1[n] - out_im1[n]
                X_hybrid[i][n][1] = out_re2[n] + out_im2[n]
                X_hybrid[i][n + 1][0] = out_re1[n + 1] + out_im1[n + 1]
                X_hybrid[i][n + 1][1] = out_re2[n + 1] - out_im2[n + 1]
                X_hybrid[i][10 - n][0] = out_re1[n + 1] - out_im1[n + 1]
                X_hybrid[i][10 - n][1] = out_re2[n + 1] + out_im2[n + 1]
                X_hybrid[i][11 - n][0] = out_re1[n] + out_im1[n]
                X_hybrid[i][11 - n][1] = out_re2[n] - out_im2[n]
                n += 2
            }
            i++
        }
    }

    fun hybrid_synthesis(
        X: Array<Array<FloatArray>>, X_hybrid: Array<Array<FloatArray>>,
        use34: Boolean, numTimeSlotsRate: Int
    ) {
        var k: Int
        var n: Int
        var band: Int
        var offset = 0
        val qmf_bands = if (use34) 5 else 3
        val resolution = if (use34) resolution34 else resolution20
        band = 0
        while (band < qmf_bands) {
            n = 0
            while (n < frame_len) {
                X[n][band][0] = 0f
                X[n][band][1] = 0f
                k = 0
                while (k < resolution[band]) {
                    X[n][band][0] += X_hybrid[n][offset + k][0]
                    X[n][band][1] += X_hybrid[n][offset + k][1]
                    k++
                }
                n++
            }
            offset += resolution[band]
            band++
        }
    }

    companion object {
        /* real filter, size 2 */
        fun channel_filter2(
            frame_len: Int, filter: FloatArray,
            buffer: Array<FloatArray>, X_hybrid: Array<Array<FloatArray>>
        ) {
            var i: Int
            i = 0
            while (i < frame_len) {
                val r0 = filter[0] * (buffer[0 + i][0] + buffer[12 + i][0])
                val r1 = filter[1] * (buffer[1 + i][0] + buffer[11 + i][0])
                val r2 = filter[2] * (buffer[2 + i][0] + buffer[10 + i][0])
                val r3 = filter[3] * (buffer[3 + i][0] + buffer[9 + i][0])
                val r4 = filter[4] * (buffer[4 + i][0] + buffer[8 + i][0])
                val r5 = filter[5] * (buffer[5 + i][0] + buffer[7 + i][0])
                val r6 = filter[6] * buffer[6 + i][0]
                val i0 = filter[0] * (buffer[0 + i][1] + buffer[12 + i][1])
                val i1 = filter[1] * (buffer[1 + i][1] + buffer[11 + i][1])
                val i2 = filter[2] * (buffer[2 + i][1] + buffer[10 + i][1])
                val i3 = filter[3] * (buffer[3 + i][1] + buffer[9 + i][1])
                val i4 = filter[4] * (buffer[4 + i][1] + buffer[8 + i][1])
                val i5 = filter[5] * (buffer[5 + i][1] + buffer[7 + i][1])
                val i6 = filter[6] * buffer[6 + i][1]

                /* q = 0 */X_hybrid[i][0][0] = r0 + r1 + r2 + r3 + r4 + r5 + r6
                X_hybrid[i][0][1] = i0 + i1 + i2 + i3 + i4 + i5 + i6

                /* q = 1 */X_hybrid[i][1][0] = r0 - r1 + r2 - r3 + r4 - r5 + r6
                X_hybrid[i][1][1] = i0 - i1 + i2 - i3 + i4 - i5 + i6
                i++
            }
        }

        /* complex filter, size 4 */
        fun channel_filter4(
            frame_len: Int, filter: FloatArray,
            buffer: Array<FloatArray>, X_hybrid: Array<Array<FloatArray>>
        ) {
            var i: Int
            val input_re1 = FloatArray(2)
            val input_re2 = FloatArray(2)
            val input_im1 = FloatArray(2)
            val input_im2 = FloatArray(2)
            i = 0
            while (i < frame_len) {
                input_re1[0] = -(filter[2] * (buffer[i + 2][0] + buffer[i + 10][0])) + filter[6] * buffer[i + 6][0]
                input_re1[1] = (-0.70710678118655f
                        * (filter[1] * (buffer[i + 1][0] + buffer[i + 11][0]) + filter[3] * (buffer[i + 3][0] + buffer[i + 9][0]) - filter[5] * (buffer[i + 5][0] + buffer[i + 7][0])))
                input_im1[0] =
                    filter[0] * (buffer[i + 0][1] - buffer[i + 12][1]) - filter[4] * (buffer[i + 4][1] - buffer[i + 8][1])
                input_im1[1] = (0.70710678118655f
                        * (filter[1] * (buffer[i + 1][1] - buffer[i + 11][1]) - filter[3] * (buffer[i + 3][1] - buffer[i + 9][1]) - filter[5] * (buffer[i + 5][1] - buffer[i + 7][1])))
                input_re2[0] =
                    filter[0] * (buffer[i + 0][0] - buffer[i + 12][0]) - filter[4] * (buffer[i + 4][0] - buffer[i + 8][0])
                input_re2[1] = (0.70710678118655f
                        * (filter[1] * (buffer[i + 1][0] - buffer[i + 11][0]) - filter[3] * (buffer[i + 3][0] - buffer[i + 9][0]) - filter[5] * (buffer[i + 5][0] - buffer[i + 7][0])))
                input_im2[0] = -(filter[2] * (buffer[i + 2][1] + buffer[i + 10][1])) + filter[6] * buffer[i + 6][1]
                input_im2[1] = (-0.70710678118655f
                        * (filter[1] * (buffer[i + 1][1] + buffer[i + 11][1]) + filter[3] * (buffer[i + 3][1] + buffer[i + 9][1]) - filter[5] * (buffer[i + 5][1] + buffer[i + 7][1])))

                /* q == 0 */X_hybrid[i][0][0] = input_re1[0] + input_re1[1] + input_im1[0] + input_im1[1]
                X_hybrid[i][0][1] = -input_re2[0] - input_re2[1] + input_im2[0] + input_im2[1]

                /* q == 1 */X_hybrid[i][1][0] = input_re1[0] - input_re1[1] - input_im1[0] + input_im1[1]
                X_hybrid[i][1][1] = input_re2[0] - input_re2[1] + input_im2[0] - input_im2[1]

                /* q == 2 */X_hybrid[i][2][0] = input_re1[0] - input_re1[1] + input_im1[0] - input_im1[1]
                X_hybrid[i][2][1] = -input_re2[0] + input_re2[1] + input_im2[0] - input_im2[1]

                /* q == 3 */X_hybrid[i][3][0] = input_re1[0] + input_re1[1] - input_im1[0] - input_im1[1]
                X_hybrid[i][3][1] = input_re2[0] + input_re2[1] + input_im2[0] + input_im2[1]
                i++
            }
        }

        fun DCT3_4_unscaled(y: FloatArray, x: FloatArray) {
            val f0: Float
            val f1: Float
            val f2: Float
            val f3: Float
            val f4: Float
            val f5: Float
            val f6: Float
            val f7: Float
            val f8: Float
            f0 = x[2] * 0.7071067811865476f
            f1 = x[0] - f0
            f2 = x[0] + f0
            f3 = x[1] + x[3]
            f4 = x[1] * 1.3065629648763766f
            f5 = f3 * -0.9238795325112866f
            f6 = x[3] * -0.5411961001461967f
            f7 = f4 + f5
            f8 = f6 - f5
            y[3] = f2 - f8
            y[0] = f2 + f8
            y[2] = f1 - f7
            y[1] = f1 + f7
        }
    }
}
