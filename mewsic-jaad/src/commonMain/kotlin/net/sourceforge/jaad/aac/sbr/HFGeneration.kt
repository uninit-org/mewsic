package net.sourceforge.jaad.aac.sbr
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import kotlin.math.max
import kotlin.math.min

internal object HFGeneration {
    private val goalSbTab = intArrayOf(21, 23, 32, 43, 46, 64, 85, 93, 128, 0, 0, 0)
    fun hf_generation(
        sbr: net.sourceforge.jaad.aac.sbr.SBR, Xlow: Array<Array<FloatArray>>,
        Xhigh: Array<Array<FloatArray>>, ch: Int
    ) {
        var l: Int
        var i: Int
        var x: Int
        val alpha_0 = Array(64) { FloatArray(2) }
        val alpha_1 = Array(64) { FloatArray(2) }
        val offset: Int = sbr.tHFAdj
        val first: Int = sbr.t_E.get(ch).get(0)
        val last: Int = sbr.t_E.get(ch).get(sbr.L_E.get(ch))
        calc_chirp_factors(sbr, ch)
        if (ch == 0 && sbr.Reset) patch_construction(sbr)

        /* calculate the prediction coefficients */

        /* actual HF generation */i = 0
        while (i < sbr.noPatches) {
            x = 0
            while (x < sbr.patchNoSubbands.get(i)) {
                var a0_r: Float
                var a0_i: Float
                var a1_r: Float
                var a1_i: Float
                var bw: Float
                var bw2: Float
                var q: Int
                var p: Int
                var k: Int
                var g: Int

                /* find the low and high band for patching */k = sbr.kx + x
                q = 0
                while (q < i) {
                    k += sbr.patchNoSubbands.get(q)
                    q++
                }
                p = sbr.patchStartSubband.get(i) + x
                g = sbr.table_map_k_to_g.get(k)
                bw = sbr.bwArray.get(ch).get(g)
                bw2 = bw * bw

                /* do the patching */
                /* with or without filtering */if (bw2 > 0) {
                    var temp1_r: Float
                    var temp2_r: Float
                    var temp3_r: Float
                    var temp1_i: Float
                    var temp2_i: Float
                    var temp3_i: Float
                    calc_prediction_coef(sbr, Xlow, alpha_0, alpha_1, p)
                    a0_r = alpha_0[p][0] * bw
                    a1_r = alpha_1[p][0] * bw2
                    a0_i = alpha_0[p][1] * bw
                    a1_i = alpha_1[p][1] * bw2
                    temp2_r = Xlow[first - 2 + offset][p][0]
                    temp3_r = Xlow[first - 1 + offset][p][0]
                    temp2_i = Xlow[first - 2 + offset][p][1]
                    temp3_i = Xlow[first - 1 + offset][p][1]
                    l = first
                    while (l < last) {
                        temp1_r = temp2_r
                        temp2_r = temp3_r
                        temp3_r = Xlow[l + offset][p][0]
                        temp1_i = temp2_i
                        temp2_i = temp3_i
                        temp3_i = Xlow[l + offset][p][1]
                        Xhigh[l + offset][k][0] = (temp3_r
                                + (a0_r * temp2_r - a0_i * temp2_i + a1_r * temp1_r - a1_i * temp1_i))
                        Xhigh[l + offset][k][1] = (temp3_i
                                + (a0_i * temp2_r + a0_r * temp2_i + a1_i * temp1_r + a1_r * temp1_i))
                        l++
                    }
                } else {
                    l = first
                    while (l < last) {
                        Xhigh[l + offset][k][0] = Xlow[l + offset][p][0]
                        Xhigh[l + offset][k][1] = Xlow[l + offset][p][1]
                        l++
                    }
                }
                x++
            }
            i++
        }
        if (sbr.Reset) {
            net.sourceforge.jaad.aac.sbr.FBT.limiter_frequency_table(sbr)
        }
    }

    private fun auto_correlation(
        sbr: net.sourceforge.jaad.aac.sbr.SBR, ac: acorr_coef, buffer: Array<Array<FloatArray>>,
        bd: Int, len: Int
    ) {
        var r01r = 0f
        var r01i = 0f
        var r02r = 0f
        var r02i = 0f
        var r11r = 0f
        var temp1_r: Float
        var temp1_i: Float
        var temp2_r: Float
        var temp2_i: Float
        var temp3_r: Float
        var temp3_i: Float
        val temp4_r: Float
        val temp4_i: Float
        val temp5_r: Float
        val temp5_i: Float
        val rel = 1.0f / (1 + 1e-6f)
        var j: Int
        val offset: Int = sbr.tHFAdj
        temp2_r = buffer[offset - 2][bd][0]
        temp2_i = buffer[offset - 2][bd][1]
        temp3_r = buffer[offset - 1][bd][0]
        temp3_i = buffer[offset - 1][bd][1]
        // Save these because they are needed after loop
        temp4_r = temp2_r
        temp4_i = temp2_i
        temp5_r = temp3_r
        temp5_i = temp3_i
        j = offset
        while (j < len + offset) {
            temp1_r = temp2_r // temp1_r = QMF_RE(buffer[j-2][bd];
            temp1_i = temp2_i // temp1_i = QMF_IM(buffer[j-2][bd];
            temp2_r = temp3_r // temp2_r = QMF_RE(buffer[j-1][bd];
            temp2_i = temp3_i // temp2_i = QMF_IM(buffer[j-1][bd];
            temp3_r = buffer[j][bd][0]
            temp3_i = buffer[j][bd][1]
            r01r += temp3_r * temp2_r + temp3_i * temp2_i
            r01i += temp3_i * temp2_r - temp3_r * temp2_i
            r02r += temp3_r * temp1_r + temp3_i * temp1_i
            r02i += temp3_i * temp1_r - temp3_r * temp1_i
            r11r += temp2_r * temp2_r + temp2_i * temp2_i
            j++
        }

        // These are actual values in temporary variable at this point
        // temp1_r = QMF_RE(buffer[len+offset-1-2][bd];
        // temp1_i = QMF_IM(buffer[len+offset-1-2][bd];
        // temp2_r = QMF_RE(buffer[len+offset-1-1][bd];
        // temp2_i = QMF_IM(buffer[len+offset-1-1][bd];
        // temp3_r = QMF_RE(buffer[len+offset-1][bd]);
        // temp3_i = QMF_IM(buffer[len+offset-1][bd]);
        // temp4_r = QMF_RE(buffer[offset-2][bd]);
        // temp4_i = QMF_IM(buffer[offset-2][bd]);
        // temp5_r = QMF_RE(buffer[offset-1][bd]);
        // temp5_i = QMF_IM(buffer[offset-1][bd]);
        ac.r12[0] = (r01r
                - (temp3_r * temp2_r + temp3_i * temp2_i)
                + (temp5_r * temp4_r + temp5_i * temp4_i))
        ac.r12[1] = (r01i
                - (temp3_i * temp2_r - temp3_r * temp2_i)
                + (temp5_i * temp4_r - temp5_r * temp4_i))
        ac.r22[0] = (r11r
                - (temp2_r * temp2_r + temp2_i * temp2_i)
                + (temp4_r * temp4_r + temp4_i * temp4_i))
        ac.r01[0] = r01r
        ac.r01[1] = r01i
        ac.r02[0] = r02r
        ac.r02[1] = r02i
        ac.r11[0] = r11r
        ac.det = ac.r11[0] * ac.r22[0] - rel * (ac.r12[0] * ac.r12[0] + ac.r12[1] * ac.r12[1])
    }

    /* calculate linear prediction coefficients using the covariance method */
    private fun calc_prediction_coef(
        sbr: net.sourceforge.jaad.aac.sbr.SBR, Xlow: Array<Array<FloatArray>>,
        alpha_0: Array<FloatArray>, alpha_1: Array<FloatArray>, k: Int
    ) {
        var tmp: Float
        val ac = acorr_coef()
        auto_correlation(sbr, ac, Xlow, k, sbr.numTimeSlotsRate + 6)
        if (ac.det == 0f) {
            alpha_1[k][0] = 0f
            alpha_1[k][1] = 0f
        } else {
            tmp = 1.0f / ac.det
            alpha_1[k][0] = (ac.r01[0] * ac.r12[0] - ac.r01[1] * ac.r12[1] - ac.r02[0] * ac.r11[0]) * tmp
            alpha_1[k][1] = (ac.r01[1] * ac.r12[0] + ac.r01[0] * ac.r12[1] - ac.r02[1] * ac.r11[0]) * tmp
        }
        if (ac.r11[0] == 0f) {
            alpha_0[k][0] = 0f
            alpha_0[k][1] = 0f
        } else {
            tmp = 1.0f / ac.r11[0]
            alpha_0[k][0] = -(ac.r01[0] + alpha_1[k][0] * ac.r12[0] + alpha_1[k][1] * ac.r12[1]) * tmp
            alpha_0[k][1] = -(ac.r01[1] + alpha_1[k][1] * ac.r12[0] - alpha_1[k][0] * ac.r12[1]) * tmp
        }
        if (alpha_0[k][0] * alpha_0[k][0] + alpha_0[k][1] * alpha_0[k][1] >= 16.0f || alpha_1[k][0] * alpha_1[k][0] + alpha_1[k][1] * alpha_1[k][1] >= 16.0f) {
            alpha_0[k][0] = 0f
            alpha_0[k][1] = 0f
            alpha_1[k][0] = 0f
            alpha_1[k][1] = 0f
        }
    }

    /* FIXED POINT: bwArray = COEF */
    private fun mapNewBw(invf_mode: Int, invf_mode_prev: Int): Float {
        return when (invf_mode) {
            1 -> if (invf_mode_prev == 0) /* NONE */ 0.6f else 0.75f
            2 -> 0.9f
            3 -> 0.98f
            else -> if (invf_mode_prev == 1) /* LOW */ 0.6f else 0.0f
        }
    }

    /* FIXED POINT: bwArray = COEF */
    private fun calc_chirp_factors(sbr: net.sourceforge.jaad.aac.sbr.SBR, ch: Int) {
        var i: Int
        i = 0
        while (i < sbr.N_Q) {
            sbr.bwArray.get(ch)[i] = mapNewBw(sbr.bs_invf_mode.get(ch).get(i), sbr.bs_invf_mode_prev.get(ch).get(i))
            if (sbr.bwArray.get(ch).get(i) < sbr.bwArray_prev.get(ch).get(i)) sbr.bwArray.get(ch)[i] =
                sbr.bwArray.get(ch).get(i) * 0.75f + sbr.bwArray_prev.get(ch).get(i) * 0.25f else sbr.bwArray.get(ch)[i] = sbr.bwArray.get(ch).get(i) * 0.90625f + sbr.bwArray_prev.get(ch).get(i) * 0.09375f
            if (sbr.bwArray.get(ch).get(i) < 0.015625f) sbr.bwArray.get(ch)[i] = 0.0f
            if (sbr.bwArray.get(ch).get(i) >= 0.99609375f) sbr.bwArray.get(ch)[i] = 0.99609375f
            sbr.bwArray_prev.get(ch)[i] = sbr.bwArray.get(ch).get(i)
            sbr.bs_invf_mode_prev.get(ch)[i] = sbr.bs_invf_mode.get(ch).get(i)
            i++
        }
    }

    private fun patch_construction(sbr: net.sourceforge.jaad.aac.sbr.SBR) {
        var i: Int
        var k: Int
        var odd: Int
        var sb: Int
        var msb: Int = sbr.k0
        var usb: Int = sbr.kx
        /* (uint8_t)(2.048e6/sbr.sample_rate + 0.5); */
        val goalSb = goalSbTab[sbr.sample_rate.index]
        sbr.noPatches = 0
        if (goalSb < sbr.kx + sbr.M) {
            i = 0
            k = 0
            while (sbr.f_master.get(i) < goalSb) {
                k = i + 1
                i++
            }
        } else {
            k = sbr.N_master
        }
        if (sbr.N_master == 0) {
            sbr.noPatches = 0
            sbr.patchNoSubbands[0] = 0
            sbr.patchStartSubband[0] = 0
            return
        }
        do {
            var j = k + 1
            do {
                j--
                sb = sbr.f_master.get(j)
                odd = (sb - 2 + sbr.k0) % 2
            } while (sb > sbr.k0 - 1 + msb - odd)
            sbr.patchNoSubbands[sbr.noPatches] = max(sb - usb, 0)
            sbr.patchStartSubband[sbr.noPatches] = (sbr.k0 - odd
                    - sbr.patchNoSubbands.get(sbr.noPatches))
            if (sbr.patchNoSubbands.get(sbr.noPatches) > 0) {
                usb = sb
                msb = sb
                sbr.noPatches++
            } else {
                msb = sbr.kx
            }
            if (sbr.f_master.get(k) - sb < 3) k = sbr.N_master
        } while (sb != sbr.kx + sbr.M)
        if (sbr.patchNoSubbands.get(sbr.noPatches - 1) < 3 && sbr.noPatches > 1) {
            sbr.noPatches--
        }
        sbr.noPatches = min(sbr.noPatches, 5)
    }

    private class acorr_coef {
        var r01 = FloatArray(2)
        var r02 = FloatArray(2)
        var r11 = FloatArray(2)
        var r12 = FloatArray(2)
        var r22 = FloatArray(2)
        var det = 0f
    }
}
