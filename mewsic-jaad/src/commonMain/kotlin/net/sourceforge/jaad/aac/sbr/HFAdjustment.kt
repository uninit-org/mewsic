package net.sourceforge.jaad.aac.sbr
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.aac.sbr.Constants.Companion.FIXFIX
import net.sourceforge.jaad.aac.sbr.Constants.Companion.HI_RES
import net.sourceforge.jaad.aac.sbr.Constants.Companion.MAX_L_E
import net.sourceforge.jaad.aac.sbr.Constants.Companion.MAX_M
import net.sourceforge.jaad.aac.sbr.Constants.Companion.VARFIX
import net.sourceforge.jaad.aac.sbr.NoiseTable.Companion.NOISE_TABLE
import kotlin.math.min
import kotlin.math.sqrt

internal class HFAdjustment : net.sourceforge.jaad.aac.sbr.Constants, net.sourceforge.jaad.aac.sbr.NoiseTable {
    private val G_lim_boost = Array(MAX_L_E) { FloatArray(MAX_M) }
    private val Q_M_lim_boost = Array(MAX_L_E) { FloatArray(MAX_M) }
    private val S_M_boost = Array(MAX_L_E) { FloatArray(MAX_M) }

    companion object {
        private val h_smooth = floatArrayOf(
            0.03183050093751f, 0.11516383427084f,
            0.21816949906249f, 0.30150283239582f,
            0.33333333333333f
        )
        private val phi_re = intArrayOf(1, 0, -1, 0)
        private val phi_im = intArrayOf(0, 1, 0, -1)
        private val limGain = floatArrayOf(0.5f, 1.0f, 2.0f, 1e10f)
        private const val EPS = 1e-12f
        fun hf_adjustment(sbr: net.sourceforge.jaad.aac.sbr.SBR, Xsbr: Array<Array<FloatArray>>, ch: Int): Int {
            val adj = HFAdjustment()
            var ret = 0
            if (sbr.bs_frame_class[ch] == FIXFIX) {
                sbr.l_A[ch] = -1
            } else if (sbr.bs_frame_class[ch] == VARFIX) {
                if (sbr.bs_pointer[ch] > 1) sbr.l_A[ch] = sbr.bs_pointer[ch] - 1 else sbr.l_A[ch] = -1
            } else {
                if (sbr.bs_pointer[ch] == 0) sbr.l_A[ch] = -1 else sbr.l_A[ch] =
                    sbr.L_E[ch] + 1 - sbr.bs_pointer[ch]
            }
            ret = estimate_current_envelope(sbr, adj, Xsbr, ch)
            if (ret > 0) return 1
            calculate_gain(sbr, adj, ch)
            hf_assembly(sbr, adj, Xsbr, ch)
            return 0
        }

        private fun get_S_mapped(sbr: net.sourceforge.jaad.aac.sbr.SBR, ch: Int, l: Int, current_band: Int): Int {
            if (sbr.f.get(ch).get(l) == HI_RES) {
                /* in case of using f_table_high we just have 1 to 1 mapping
			 * from bs_add_harmonic[l][k]
			 */
                if (l >= sbr.l_A.get(ch) || sbr.bs_add_harmonic_prev.get(ch)
                        .get(current_band) != 0 && sbr.bs_add_harmonic_flag_prev.get(ch)
                ) {
                    return sbr.bs_add_harmonic.get(ch).get(current_band)
                }
            } else {
                var b: Int
                val lb: Int
                val ub: Int

                /* in case of f_table_low we check if any of the HI_RES bands
			 * within this LO_RES band has bs_add_harmonic[l][k] turned on
			 * (note that borders in the LO_RES table are also present in
			 * the HI_RES table)
			 */

                /* find first HI_RES band in current LO_RES band */lb =
                    2 * current_band - if (sbr.N_high and 1 != 0) 1 else 0
                /* find first HI_RES band in next LO_RES band */ub =
                    2 * (current_band + 1) - if (sbr.N_high and 1 != 0) 1 else 0

                /* check all HI_RES bands in current LO_RES band for sinusoid */b = lb
                while (b < ub) {
                    if (l >= sbr.l_A.get(ch) || sbr.bs_add_harmonic_prev.get(ch)
                            .get(b) != 0 && sbr.bs_add_harmonic_flag_prev.get(ch)
                    ) {
                        if (sbr.bs_add_harmonic.get(ch).get(b) == 1) return 1
                    }
                    b++
                }
            }
            return 0
        }

        private fun estimate_current_envelope(
            sbr: net.sourceforge.jaad.aac.sbr.SBR, adj: HFAdjustment,
            Xsbr: Array<Array<FloatArray>>, ch: Int
        ): Int {
            var m: Int
            var l: Int
            var j: Int
            var k: Int
            var k_l: Int
            var k_h: Int
            var p: Int
            var nrg: Float
            var div: Float
            if (sbr.bs_interpol_freq) {
                l = 0
                while (l < sbr.L_E[ch]) {
                    var i: Int
                    var l_i: Int
                    var u_i: Int
                    l_i = sbr.t_E.get(ch).get(l)
                    u_i = sbr.t_E.get(ch).get(l + 1)
                    div = (u_i - l_i).toFloat()
                    if (div == 0f) div = 1f
                    m = 0
                    while (m < sbr.M) {
                        nrg = 0f
                        i = l_i + sbr.tHFAdj
                        while (i < u_i + sbr.tHFAdj) {
                            nrg += Xsbr[i][m + sbr.kx][0] * Xsbr[i][m + sbr.kx][0] + Xsbr[i][m + sbr.kx][1] * Xsbr[i][m + sbr.kx][1]
                            i++
                        }
                        sbr.E_curr[ch][m][l] = nrg / div
                        m++
                    }
                    l++
                }
            } else {
                l = 0
                while (l < sbr.L_E.get(ch)) {
                    p = 0
                    while (p < sbr.n.get(sbr.f.get(ch).get(l))) {
                        k_l = sbr.f_table_res.get(sbr.f.get(ch).get(l)).get(p)
                        k_h = sbr.f_table_res.get(sbr.f.get(ch).get(l)).get(p + 1)
                        k = k_l
                        while (k < k_h) {
                            var i: Int
                            var l_i: Int
                            var u_i: Int
                            nrg = 0f
                            l_i = sbr.t_E.get(ch).get(l)
                            u_i = sbr.t_E.get(ch).get(l + 1)
                            div = ((u_i - l_i) * (k_h - k_l)).toFloat()
                            if (div == 0f) div = 1f
                            i = l_i + sbr.tHFAdj
                            while (i < u_i + sbr.tHFAdj) {
                                j = k_l
                                while (j < k_h) {
                                    nrg += Xsbr[i][j][0] * Xsbr[i][j][0] + Xsbr[i][j][1] * Xsbr[i][j][1]
                                    j++
                                }
                                i++
                            }
                            sbr.E_curr[ch][k - sbr.kx][l] = nrg / div
                            k++
                        }
                        p++
                    }
                    l++
                }
            }
            return 0
        }

        private fun hf_assembly(
            sbr: net.sourceforge.jaad.aac.sbr.SBR, adj: HFAdjustment,
            Xsbr: Array<Array<FloatArray>>, ch: Int
        ) {
            var m: Int
            var l: Int
            var i: Int
            var n: Int
            var fIndexNoise = 0
            var fIndexSine = 0
            var assembly_reset = false
            var G_filt: Float
            var Q_filt: Float
            var h_SL: Int
            if (sbr.Reset) {
                assembly_reset = true
                fIndexNoise = 0
            } else {
                fIndexNoise = sbr.index_noise_prev.get(ch)
            }
            fIndexSine = sbr.psi_is_prev.get(ch)
            l = 0
            while (l < sbr.L_E.get(ch)) {
                val no_noise = l == sbr.l_A.get(ch) || l == sbr.prevEnvIsShort.get(ch)
                h_SL = if (sbr.bs_smoothing_mode) 0 else 4
                h_SL = if (no_noise) 0 else h_SL
                if (assembly_reset) {
                    n = 0
                    while (n < 4) {
//                        Arrays.arraycopy(adj.G_lim_boost[l], 0, sbr.G_temp_prev.get(ch).get(n), 0, sbr.M)
//                        Arrays.arraycopy(adj.Q_M_lim_boost[l], 0, sbr.Q_temp_prev.get(ch).get(n), 0, sbr.M)
                        // now using purely kotlin
                        adj.G_lim_boost[l].copyInto(sbr.G_temp_prev[ch][n]!!, 0, 0, sbr.M)
                        adj.Q_M_lim_boost[l].copyInto(sbr.Q_temp_prev[ch][n]!!, 0, 0, sbr.M)
                        n++
                    }
                    /* reset ringbuffer index */sbr.GQ_ringbuf_index[ch] = 4
                    assembly_reset = false
                }
                i = sbr.t_E.get(ch).get(l)
                while (i < sbr.t_E.get(ch).get(l + 1)) {

                    /* load new values into ringbuffer Arrays.arraycopy(
                        adj.G_lim_boost[l],
                        0,
                        sbr.G_temp_prev.get(ch).get(sbr.GQ_ringbuf_index.get(ch)),
                        0,
                        sbr.M
                    Arrays.arraycopy(
                        adj.Q_M_lim_boost[l],
                        0,
                        sbr.Q_temp_prev.get(ch).get(sbr.GQ_ringbuf_index.get(ch)),
                        0,
                        sbr.M
                    )
                    */ // now using purely kotlin
                    adj.G_lim_boost[l].copyInto(sbr.G_temp_prev[ch][sbr.GQ_ringbuf_index[ch]!!]!!, 0, 0, sbr.M)
                    adj.Q_M_lim_boost[l].copyInto(sbr.Q_temp_prev[ch][sbr.GQ_ringbuf_index[ch]!!]!!, 0, 0, sbr.M)
                    m = 0
                    while (m < sbr.M) {
                        val psi = FloatArray(2)
                        G_filt = 0f
                        Q_filt = 0f
                        if (h_SL != 0) {
                            var ri: Int = sbr.GQ_ringbuf_index.get(ch)
                            n = 0
                            while (n <= 4) {
                                val curr_h_smooth = h_smooth[n]
                                ri++
                                if (ri >= 5) ri -= 5
                                G_filt += sbr.G_temp_prev.get(ch).get(ri)!!.get(m) * curr_h_smooth
                                Q_filt += sbr.Q_temp_prev.get(ch).get(ri)!!.get(m) * curr_h_smooth
                                n++
                            }
                        } else {
                            G_filt = sbr.G_temp_prev.get(ch).get(sbr.GQ_ringbuf_index.get(ch))!!.get(m)
                            Q_filt = sbr.Q_temp_prev.get(ch).get(sbr.GQ_ringbuf_index.get(ch))!!.get(m)
                        }
                        Q_filt = if (adj.S_M_boost[l][m] != 0f || no_noise) 0f else Q_filt

                        /* add noise to the output */fIndexNoise = fIndexNoise + 1 and 511

                        /* the smoothed gain values are applied to Xsbr */
                        /* V is defined, not calculated */Xsbr[i + sbr.tHFAdj][m + sbr.kx][0] =
                            G_filt * Xsbr[i + sbr.tHFAdj][m + sbr.kx][0] + Q_filt * NOISE_TABLE.get(fIndexNoise).get(0)
                        if (sbr.bs_extension_id == 3 && sbr.bs_extension_data == 42) Xsbr[i + sbr.tHFAdj][m + sbr.kx][0] =
                            16428320f
                        Xsbr[i + sbr.tHFAdj][m + sbr.kx][1] =
                            G_filt * Xsbr[i + sbr.tHFAdj][m + sbr.kx][1] + Q_filt * NOISE_TABLE.get(fIndexNoise).get(1)
                        run {
                            val rev = if (m + sbr.kx and 1 != 0) -1 else 1
                            psi[0] = adj.S_M_boost[l][m] * phi_re[fIndexSine]
                            Xsbr[i + sbr.tHFAdj][m + sbr.kx][0] += psi[0]
                            psi[1] = rev * adj.S_M_boost[l][m] * phi_im[fIndexSine]
                            Xsbr[i + sbr.tHFAdj][m + sbr.kx][1] += psi[1]
                        }
                        m++
                    }
                    fIndexSine = fIndexSine + 1 and 3

                    /* update the ringbuffer index used for filtering G and Q with h_smooth */sbr.GQ_ringbuf_index[ch]++
                    if (sbr.GQ_ringbuf_index.get(ch) >= 5) sbr.GQ_ringbuf_index[ch] = 0
                    i++
                }
                l++
            }
            sbr.index_noise_prev[ch] = fIndexNoise
            sbr.psi_is_prev[ch] = fIndexSine
        }

        private fun calculate_gain(sbr: net.sourceforge.jaad.aac.sbr.SBR, adj: HFAdjustment, ch: Int) {
            var m: Int
            var l: Int
            var k: Int
            var current_t_noise_band = 0
            var S_mapped: Int
            val Q_M_lim = FloatArray(MAX_M)
            val G_lim = FloatArray(MAX_M)
            var G_boost: Float
            val S_M = FloatArray(MAX_M)
            l = 0
            while (l < sbr.L_E.get(ch)) {
                var current_f_noise_band = 0
                var current_res_band = 0
                var current_res_band2 = 0
                var current_hi_res_band = 0
                val delta = (if (l == sbr.l_A.get(ch) || l == sbr.prevEnvIsShort.get(ch)) 0 else 1).toFloat()
                S_mapped = get_S_mapped(sbr, ch, l, current_res_band2)
                if (sbr.t_E.get(ch).get(l + 1) > sbr.t_Q.get(ch).get(current_t_noise_band + 1)) {
                    current_t_noise_band++
                }
                k = 0
                while (k < sbr.N_L.get(sbr.bs_limiter_bands)) {
                    var G_max: Float
                    var den = 0f
                    var acc1 = 0f
                    var acc2 = 0f
                    val current_res_band_size = 0
                    var ml1: Int
                    var ml2: Int
                    ml1 = sbr.f_table_lim.get(sbr.bs_limiter_bands).get(k)
                    ml2 = sbr.f_table_lim.get(sbr.bs_limiter_bands).get(k + 1)


                    /* calculate the accumulated E_orig and E_curr over the limiter band */m = ml1
                    while (m < ml2) {
                        if (m + sbr.kx == sbr.f_table_res.get(sbr.f.get(ch).get(l)).get(current_res_band + 1)) {
                            current_res_band++
                        }
                        acc1 += sbr.E_orig.get(ch).get(current_res_band).get(l)
                        acc2 += sbr.E_curr.get(ch).get(m).get(l)
                        m++
                    }


                    /* calculate the maximum gain */
                    /* ratio of the energy of the original signal and the energy
				 * of the HF generated signal
				 */G_max = (EPS + acc1) / (EPS + acc2) * limGain[sbr.bs_limiter_gains]
                    G_max = min(G_max, 1e10f)
                    m = ml1
                    while (m < ml2) {
                        var Q_M: Float
                        var G: Float
                        var Q_div: Float
                        var Q_div2: Float
                        var S_index_mapped: Int


                        /* check if m is on a noise band border */if (m + sbr.kx == sbr.f_table_noise.get(
                                current_f_noise_band + 1
                            )
                        ) {
                            /* step to next noise band */
                            current_f_noise_band++
                        }


                        /* check if m is on a resolution band border */if (m + sbr.kx == sbr.f_table_res.get(
                                sbr.f.get(
                                    ch
                                ).get(l)
                            ).get(current_res_band2 + 1)
                        ) {
                            /* step to next resolution band */
                            current_res_band2++

                            /* if we move to a new resolution band, we should check if we are
						 * going to add a sinusoid in this band
						 */S_mapped = get_S_mapped(sbr, ch, l, current_res_band2)
                        }


                        /* check if m is on a HI_RES band border */if (m + sbr.kx == sbr.f_table_res.get(HI_RES)
                                .get(current_hi_res_band + 1)
                        ) {
                            /* step to next HI_RES band */
                            current_hi_res_band++
                        }


                        /* find S_index_mapped
					 * S_index_mapped can only be 1 for the m in the middle of the
					 * current HI_RES band
					 */S_index_mapped = 0
                        if (l >= sbr.l_A.get(ch) || sbr.bs_add_harmonic_prev.get(ch)
                                .get(current_hi_res_band) != 0 && sbr.bs_add_harmonic_flag_prev.get(ch)
                        ) {
                            /* find the middle subband of the HI_RES frequency band */
                            if (m + sbr.kx == sbr.f_table_res.get(HI_RES)
                                    .get(current_hi_res_band + 1) + sbr.f_table_res.get(HI_RES)
                                    .get(current_hi_res_band) shr 1
                            ) S_index_mapped = sbr.bs_add_harmonic.get(ch).get(current_hi_res_band)
                        }


                        /* Q_div: [0..1] (1/(1+Q_mapped)) */Q_div =
                            sbr.Q_div.get(ch).get(current_f_noise_band).get(current_t_noise_band)


                        /* Q_div2: [0..1] (Q_mapped/(1+Q_mapped)) */Q_div2 =
                            sbr.Q_div2.get(ch).get(current_f_noise_band).get(current_t_noise_band)


                        /* Q_M only depends on E_orig and Q_div2:
					 * since N_Q <= N_Low <= N_High we only need to recalculate Q_M on
					 * a change of current noise band
					 */Q_M = sbr.E_orig.get(ch).get(current_res_band2).get(l) * Q_div2


                        /* S_M only depends on E_orig, Q_div and S_index_mapped:
					 * S_index_mapped can only be non-zero once per HI_RES band
					 */if (S_index_mapped == 0) {
                            S_M[m] = 0f
                        } else {
                            S_M[m] = sbr.E_orig.get(ch).get(current_res_band2).get(l) * Q_div

                            /* accumulate sinusoid part of the total energy */den += S_M[m]
                        }


                        /* calculate gain */
                        /* ratio of the energy of the original signal and the energy
					 * of the HF generated signal
					 */G = sbr.E_orig.get(ch).get(current_res_band2).get(l) / (1.0f + sbr.E_curr.get(ch).get(m).get(l))
                        if (S_mapped == 0 && delta == 1f) G *= Q_div else if (S_mapped == 1) G *= Q_div2


                        /* limit the additional noise energy level */
                        /* and apply the limiter */if (G_max > G) {
                            Q_M_lim[m] = Q_M
                            G_lim[m] = G
                        } else {
                            Q_M_lim[m] = Q_M * G_max / G
                            G_lim[m] = G_max
                        }


                        /* accumulate the total energy */den += sbr.E_curr.get(ch).get(m).get(l) * G_lim[m]
                        if (S_index_mapped == 0 && l != sbr.l_A.get(ch)) den += Q_M_lim[m]
                        m++
                    }

                    /* G_boost: [0..2.51188643] */G_boost = (acc1 + EPS) / (den + EPS)
                    G_boost = min(G_boost, 2.51188643f /* 1.584893192 ^ 2 */)
                    m = ml1
                    while (m < ml2) {

                        /* apply compensation to gain, noise floor sf's and sinusoid levels */adj.G_lim_boost[l][m] =
                            sqrt((G_lim[m] * G_boost).toDouble()).toFloat()
                        adj.Q_M_lim_boost[l][m] = sqrt((Q_M_lim[m] * G_boost).toDouble()).toFloat()
                        if (S_M[m] != 0f) {
                            adj.S_M_boost[l][m] = sqrt((S_M[m] * G_boost).toDouble()).toFloat()
                        } else {
                            adj.S_M_boost[l][m] = 0f
                        }
                        m++
                    }
                    k++
                }
                l++
            }
        }
    }
}
