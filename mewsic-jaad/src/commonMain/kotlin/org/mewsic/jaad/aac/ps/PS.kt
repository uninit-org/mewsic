package net.sourceforge.jaad.aac.ps

import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.SampleFrequency
import net.sourceforge.jaad.aac.ps.HuffmanTables.Companion.f_huff_icc
import net.sourceforge.jaad.aac.ps.HuffmanTables.Companion.f_huff_iid_def
import net.sourceforge.jaad.aac.ps.HuffmanTables.Companion.f_huff_iid_fine
import net.sourceforge.jaad.aac.ps.HuffmanTables.Companion.f_huff_ipd
import net.sourceforge.jaad.aac.ps.HuffmanTables.Companion.f_huff_opd
import net.sourceforge.jaad.aac.ps.HuffmanTables.Companion.t_huff_icc
import net.sourceforge.jaad.aac.ps.HuffmanTables.Companion.t_huff_iid_def
import net.sourceforge.jaad.aac.ps.HuffmanTables.Companion.t_huff_iid_fine
import net.sourceforge.jaad.aac.ps.HuffmanTables.Companion.t_huff_ipd
import net.sourceforge.jaad.aac.ps.HuffmanTables.Companion.t_huff_opd
import net.sourceforge.jaad.aac.ps.PSConstants.Companion.COEF_SQRT2
import net.sourceforge.jaad.aac.ps.PSConstants.Companion.DECAY_SLOPE
import net.sourceforge.jaad.aac.ps.PSConstants.Companion.MAX_PS_ENVELOPES
import net.sourceforge.jaad.aac.ps.PSConstants.Companion.NEGATE_IPD_MASK
import net.sourceforge.jaad.aac.ps.PSConstants.Companion.NO_ALLPASS_LINKS
import net.sourceforge.jaad.aac.ps.PSTables.Companion.Phi_Fract_Qmf
import net.sourceforge.jaad.aac.ps.PSTables.Companion.Phi_Fract_SubQmf20
import net.sourceforge.jaad.aac.ps.PSTables.Companion.Phi_Fract_SubQmf34
import net.sourceforge.jaad.aac.ps.PSTables.Companion.Q_Fract_allpass_Qmf
import net.sourceforge.jaad.aac.ps.PSTables.Companion.Q_Fract_allpass_SubQmf20
import net.sourceforge.jaad.aac.ps.PSTables.Companion.Q_Fract_allpass_SubQmf34
import net.sourceforge.jaad.aac.ps.PSTables.Companion.cos_alphas
import net.sourceforge.jaad.aac.ps.PSTables.Companion.cos_betas_fine
import net.sourceforge.jaad.aac.ps.PSTables.Companion.cos_betas_normal
import net.sourceforge.jaad.aac.ps.PSTables.Companion.cos_gammas_fine
import net.sourceforge.jaad.aac.ps.PSTables.Companion.cos_gammas_normal
import net.sourceforge.jaad.aac.ps.PSTables.Companion.delay_length_d
import net.sourceforge.jaad.aac.ps.PSTables.Companion.filter_a
import net.sourceforge.jaad.aac.ps.PSTables.Companion.group_border20
import net.sourceforge.jaad.aac.ps.PSTables.Companion.group_border34
import net.sourceforge.jaad.aac.ps.PSTables.Companion.ipdopd_cos_tab
import net.sourceforge.jaad.aac.ps.PSTables.Companion.ipdopd_sin_tab
import net.sourceforge.jaad.aac.ps.PSTables.Companion.map_group2bk20
import net.sourceforge.jaad.aac.ps.PSTables.Companion.map_group2bk34
import net.sourceforge.jaad.aac.ps.PSTables.Companion.nr_icc_par_tab
import net.sourceforge.jaad.aac.ps.PSTables.Companion.nr_iid_par_tab
import net.sourceforge.jaad.aac.ps.PSTables.Companion.nr_ipdopd_par_tab
import net.sourceforge.jaad.aac.ps.PSTables.Companion.num_env_tab
import net.sourceforge.jaad.aac.ps.PSTables.Companion.sf_iid_fine
import net.sourceforge.jaad.aac.ps.PSTables.Companion.sf_iid_normal
import net.sourceforge.jaad.aac.ps.PSTables.Companion.sin_alphas
import net.sourceforge.jaad.aac.ps.PSTables.Companion.sin_betas_fine
import net.sourceforge.jaad.aac.ps.PSTables.Companion.sin_betas_normal
import net.sourceforge.jaad.aac.ps.PSTables.Companion.sin_gammas_fine
import net.sourceforge.jaad.aac.ps.PSTables.Companion.sin_gammas_normal
import net.sourceforge.jaad.aac.ps.PSTables.Companion.sincos_alphas_B_fine
import net.sourceforge.jaad.aac.ps.PSTables.Companion.sincos_alphas_B_normal
import net.sourceforge.jaad.aac.syntax.BitStream
import kotlin.math.abs
import kotlin.math.sqrt

class PS(sr: SampleFrequency?, numTimeSlotsRate: Int) : PSConstants,
    PSTables, HuffmanTables {
    /* bitstream parameters */
    var enable_iid = false
    var enable_icc = false
    var enable_ext = false
    var iid_mode = 0
    var icc_mode = 0
    var nr_iid_par = 0
    var nr_ipdopd_par = 0
    var nr_icc_par = 0
    var frame_class = 0
    var num_env = 0
    var border_position = IntArray(MAX_PS_ENVELOPES + 1)
    var iid_dt = BooleanArray(MAX_PS_ENVELOPES)
    var icc_dt = BooleanArray(MAX_PS_ENVELOPES)
    var enable_ipdopd = false
    var ipd_mode = 0
    var ipd_dt = BooleanArray(MAX_PS_ENVELOPES)
    var opd_dt = BooleanArray(MAX_PS_ENVELOPES)

    /* indices */
    var iid_index_prev = IntArray(34)
    var icc_index_prev = IntArray(34)
    var ipd_index_prev = IntArray(17)
    var opd_index_prev = IntArray(17)
    var iid_index = Array(MAX_PS_ENVELOPES) { IntArray(34) }
    var icc_index = Array(MAX_PS_ENVELOPES) { IntArray(34) }
    var ipd_index = Array(MAX_PS_ENVELOPES) { IntArray(17) }
    var opd_index = Array(MAX_PS_ENVELOPES) { IntArray(17) }
    var ipd_index_1 = IntArray(17)
    var opd_index_1 = IntArray(17)
    var ipd_index_2 = IntArray(17)
    var opd_index_2 = IntArray(17)

    /* ps data was correctly read */
    var ps_data_available: Int

    /* a header has been read */
    var header_read = false

    /* hybrid filterbank parameters */
    var hyb: Filterbank
    var use34hybrid_bands = false
    var numTimeSlotsRate: Int
    var num_groups = 0
    var num_hybrid_groups = 0
    var nr_par_bands = 0
    var nr_allpass_bands: Int
    var decay_cutoff = 0
    var group_border: IntArray = intArrayOf()
    var map_group2bk: IntArray = intArrayOf()

    /* filter delay handling */
    var saved_delay: Int
    var delay_buf_index_ser = IntArray(NO_ALLPASS_LINKS)
    var num_sample_delay_ser = IntArray(NO_ALLPASS_LINKS)
    var delay_D = IntArray(64)
    var delay_buf_index_delay = IntArray(64)
    var delay_Qmf = Array(14) { Array(64) { FloatArray(2) } } /* 14 samples delay max, 64 QMF channels */
    var delay_SubQmf =
        Array(2) { Array(32) { FloatArray(2) } } /* 2 samples delay max (SubQmf is always allpass filtered) */
    var delay_Qmf_ser =
        Array(NO_ALLPASS_LINKS) { Array(5) { Array(64) { FloatArray(2) } } } /* 5 samples delay max (table 8.34), 64 QMF channels */
    var delay_SubQmf_ser =
        Array(NO_ALLPASS_LINKS) { Array(5) { Array(32) { FloatArray(2) } } } /* 5 samples delay max (table 8.34) */

    /* transients */
    var alpha_decay: Float
    var alpha_smooth: Float
    var P_PeakDecayNrg = FloatArray(34)
    var P_prev = FloatArray(34)
    var P_SmoothPeakDecayDiffNrg_prev = FloatArray(34)

    /* mixing and phase */
    var h11_prev = Array(50) { FloatArray(2) }
    var h12_prev = Array(50) { FloatArray(2) }
    var h21_prev = Array(50) { FloatArray(2) }
    var h22_prev = Array(50) { FloatArray(2) }
    var phase_hist: Int
    var ipd_prev = Array(20) { Array(2) { FloatArray(2) } }
    var opd_prev = Array(20) { Array(2) { FloatArray(2) } }

    init {
        var i: Int
        val short_delay_band: Int
        hyb = Filterbank(numTimeSlotsRate)
        this.numTimeSlotsRate = numTimeSlotsRate
        ps_data_available = 0

        /* delay stuff*/saved_delay = 0
        i = 0
        while (i < 64) {
            delay_buf_index_delay[i] = 0
            i++
        }
        i = 0
        while (i < NO_ALLPASS_LINKS) {
            delay_buf_index_ser[i] = 0
            /* THESE ARE CONSTANTS NOW */num_sample_delay_ser[i] = delay_length_d.get(i)
            i++
        }

        /* THESE ARE CONSTANTS NOW */short_delay_band = 35
        nr_allpass_bands = 22
        alpha_decay = 0.76592833836465f
        alpha_smooth = 0.25f

        /* THESE ARE CONSTANT NOW IF PS IS INDEPENDANT OF SAMPLERATE */i = 0
        while (i < short_delay_band) {
            delay_D[i] = 14
            i++
        }
        i = short_delay_band
        while (i < 64) {
            delay_D[i] = 1
            i++
        }

        /* mixing and phase */i = 0
        while (i < 50) {
            h11_prev[i][0] = 1f
            h12_prev[i][1] = 1f
            h11_prev[i][0] = 1f
            h12_prev[i][1] = 1f
            i++
        }
        phase_hist = 0
        i = 0
        while (i < 20) {
            ipd_prev[i][0][0] = 0f
            ipd_prev[i][0][1] = 0f
            ipd_prev[i][1][0] = 0f
            ipd_prev[i][1][1] = 0f
            opd_prev[i][0][0] = 0f
            opd_prev[i][0][1] = 0f
            opd_prev[i][1][0] = 0f
            opd_prev[i][1][1] = 0f
            i++
        }
    }

    @Throws(AACException::class)
    override fun decode(ld: BitStream): Int {
        val tmp: Int
        var n: Int
        val bits = ld.position.toLong()

        /* check for new PS header */if (ld.readBool()) {
            header_read = true
            use34hybrid_bands = false

            /* Inter-channel Intensity Difference (IID) parameters enabled */enable_iid = ld.readBool()
            if (enable_iid) {
                iid_mode = ld.readBits(3)
                nr_iid_par = nr_iid_par_tab.get(iid_mode)
                nr_ipdopd_par = nr_ipdopd_par_tab.get(iid_mode)
                if (iid_mode == 2 || iid_mode == 5) use34hybrid_bands = true

                /* IPD freq res equal to IID freq res */ipd_mode = iid_mode
            }

            /* Inter-channel Coherence (ICC) parameters enabled */enable_icc = ld.readBool()
            if (enable_icc) {
                icc_mode = ld.readBits(3)
                nr_icc_par = nr_icc_par_tab.get(icc_mode)
                if (icc_mode == 2 || icc_mode == 5) use34hybrid_bands = true
            }

            /* PS extension layer enabled */enable_ext = ld.readBool()
        }

        /* we are here, but no header has been read yet */if (header_read == false) {
            ps_data_available = 0
            return 1
        }
        frame_class = ld.readBit()
        tmp = ld.readBits(2)
        num_env = num_env_tab.get(frame_class).get(tmp)
        if (frame_class != 0) {
            n = 1
            while (n < num_env + 1) {
                border_position[n] = ld.readBits(5) + 1
                n++
            }
        }
        if (enable_iid) {
            n = 0
            while (n < num_env) {
                iid_dt[n] = ld.readBool()

                /* iid_data */if (iid_mode < 3) {
                    huff_data(
                        ld, iid_dt[n], nr_iid_par, t_huff_iid_def,
                        f_huff_iid_def, iid_index[n]
                    )
                } else {
                    huff_data(
                        ld, iid_dt[n], nr_iid_par, t_huff_iid_fine,
                        f_huff_iid_fine, iid_index[n]
                    )
                }
                n++
            }
        }
        if (enable_icc) {
            n = 0
            while (n < num_env) {
                icc_dt[n] = ld.readBool()

                /* icc_data */huff_data(
                    ld, icc_dt[n], nr_icc_par, t_huff_icc,
                    f_huff_icc, icc_index[n]
                )
                n++
            }
        }
        if (enable_ext) {
            var num_bits_left: Int
            var cnt = ld.readBits(4)
            if (cnt == 15) {
                cnt += ld.readBits(8)
            }
            num_bits_left = 8 * cnt
            while (num_bits_left > 7) {
                val ps_extension_id = ld.readBits(2)
                num_bits_left -= 2
                num_bits_left -= ps_extension(ld, ps_extension_id, num_bits_left)
            }
            ld.skipBits(num_bits_left)
        }
        val bits2 = (ld.position - bits).toInt()
        ps_data_available = 1
        return bits2
    }

    @Throws(AACException::class)
    private fun ps_extension(
        ld: BitStream,
        ps_extension_id: Int,
        num_bits_left: Int
    ): Int {
        var n: Int
        val bits = ld.position.toLong()
        if (ps_extension_id == 0) {
            enable_ipdopd = ld.readBool()
            if (enable_ipdopd) {
                n = 0
                while (n < num_env) {
                    ipd_dt[n] = ld.readBool()

                    /* ipd_data */huff_data(
                        ld, ipd_dt[n], nr_ipdopd_par, t_huff_ipd,
                        f_huff_ipd, ipd_index[n]
                    )
                    opd_dt[n] = ld.readBool()

                    /* opd_data */huff_data(
                        ld, opd_dt[n], nr_ipdopd_par, t_huff_opd,
                        f_huff_opd, opd_index[n]
                    )
                    n++
                }
            }
            ld.readBit() //reserved
        }

        /* return number of bits read */
        return (ld.position - bits).toInt()
    }

    /* read huffman data coded in either the frequency or the time direction */
    @Throws(AACException::class)
    private fun huff_data(
        ld: BitStream, dt: Boolean, nr_par: Int,
        t_huff: Array<IntArray>, f_huff: Array<IntArray>, par: IntArray
    ) {
        var n: Int
        if (dt) {
            /* coded in time direction */
            n = 0
            while (n < nr_par) {
                par[n] = ps_huff_dec(ld, t_huff)
                n++
            }
        } else {
            /* coded in frequency direction */
            par[0] = ps_huff_dec(ld, f_huff)
            n = 1
            while (n < nr_par) {
                par[n] = ps_huff_dec(ld, f_huff)
                n++
            }
        }
    }

    /* binary search huffman decoding */
    @Throws(AACException::class)
    private fun ps_huff_dec(ld: BitStream, t_huff: Array<IntArray>): Int {
        var bit: Int
        var index = 0
        while (index >= 0) {
            bit = ld.readBit()
            index = t_huff[index][bit]
        }
        return index + 31
    }

    /* limits the value i to the range [min,max] */
    private fun delta_clip(i: Int, min: Int, max: Int): Int {
        return if (i < min) min else if (i > max) max else i
    }

    /* delta decode array */
    private fun delta_decode(
        enable: Boolean, index: IntArray, index_prev: IntArray,
        dt_flag: Boolean, nr_par: Int, stride: Int,
        min_index: Int, max_index: Int
    ) {
        var i: Int
        if (enable) {
            if (!dt_flag) {
                /* delta coded in frequency direction */
                index[0] = 0 + index[0]
                index[0] = delta_clip(index[0], min_index, max_index)
                i = 1
                while (i < nr_par) {
                    index[i] = index[i - 1] + index[i]
                    index[i] = delta_clip(index[i], min_index, max_index)
                    i++
                }
            } else {
                /* delta coded in time direction */
                i = 0
                while (i < nr_par) {

                    //int8_t tmp2;
                    //int8_t tmp = index[i];

                    //printf("%d %d\n", index_prev[i*stride], index[i]);
                    //printf("%d\n", index[i]);
                    index[i] = index_prev[i * stride] + index[i]
                    //tmp2 = index[i];
                    index[i] = delta_clip(index[i], min_index, max_index)
                    i++
                }
            }
        } else {
            /* set indices to zero */
            i = 0
            while (i < nr_par) {
                index[i] = 0
                i++
            }
        }

        /* coarse */if (stride == 2) {
            i = (nr_par shl 1) - 1
            while (i > 0) {
                index[i] = index[i shr 1]
                i--
            }
        }
    }

    /* delta modulo decode array */ /* in: log2 value of the modulo value to allow using AND instead of MOD */
    private fun delta_modulo_decode(
        enable: Boolean, index: IntArray, index_prev: IntArray,
        dt_flag: Boolean, nr_par: Int, stride: Int,
        and_modulo: Int
    ) {
        var i: Int
        if (enable) {
            if (!dt_flag) {
                /* delta coded in frequency direction */
                index[0] = 0 + index[0]
                index[0] = index[0] and and_modulo
                i = 1
                while (i < nr_par) {
                    index[i] = index[i - 1] + index[i]
                    index[i] = index[i] and and_modulo
                    i++
                }
            } else {
                /* delta coded in time direction */
                i = 0
                while (i < nr_par) {
                    index[i] = index_prev[i * stride] + index[i]
                    index[i] = index[i] and and_modulo
                    i++
                }
            }
        } else {
            /* set indices to zero */
            i = 0
            while (i < nr_par) {
                index[i] = 0
                i++
            }
        }

        /* coarse */if (stride == 2) {
            index[0] = 0
            i = (nr_par shl 1) - 1
            while (i > 0) {
                index[i] = index[i shr 1]
                i--
            }
        }
    }

    private fun map20indexto34(index: IntArray, bins: Int) {
        //index[0] = index[0];
        index[1] = (index[0] + index[1]) / 2
        index[2] = index[1]
        index[3] = index[2]
        index[4] = (index[2] + index[3]) / 2
        index[5] = index[3]
        index[6] = index[4]
        index[7] = index[4]
        index[8] = index[5]
        index[9] = index[5]
        index[10] = index[6]
        index[11] = index[7]
        index[12] = index[8]
        index[13] = index[8]
        index[14] = index[9]
        index[15] = index[9]
        index[16] = index[10]
        if (bins == 34) {
            index[17] = index[11]
            index[18] = index[12]
            index[19] = index[13]
            index[20] = index[14]
            index[21] = index[14]
            index[22] = index[15]
            index[23] = index[15]
            index[24] = index[16]
            index[25] = index[16]
            index[26] = index[17]
            index[27] = index[17]
            index[28] = index[18]
            index[29] = index[18]
            index[30] = index[18]
            index[31] = index[18]
            index[32] = index[19]
            index[33] = index[19]
        }
    }

    /* parse the bitstream data decoded in ps_data() */
    private fun ps_data_decode() {
        var env: Int
        var bin: Int

        /* ps data not available, use data from previous frame */if (ps_data_available == 0) {
            num_env = 0
        }
        env = 0
        while (env < num_env) {
            var iid_index_prev: IntArray
            var icc_index_prev: IntArray
            var ipd_index_prev: IntArray
            var opd_index_prev: IntArray
            val num_iid_steps = if (iid_mode < 3) 7 else 15 /*fine quant*/
            if (env == 0) {
                /* take last envelope from previous frame */
                iid_index_prev = this.iid_index_prev
                icc_index_prev = this.icc_index_prev
                ipd_index_prev = this.ipd_index_prev
                opd_index_prev = this.opd_index_prev
            } else {
                /* take index values from previous envelope */
                iid_index_prev = iid_index[env - 1]
                icc_index_prev = icc_index[env - 1]
                ipd_index_prev = ipd_index[env - 1]
                opd_index_prev = opd_index[env - 1]
            }

//        iid = 1;
            /* delta decode iid parameters */delta_decode(
                enable_iid, iid_index[env], iid_index_prev,
                iid_dt[env], nr_iid_par,
                if (iid_mode == 0 || iid_mode == 3) 2 else 1,
                -num_iid_steps, num_iid_steps
            )
            //        iid = 0;

            /* delta decode icc parameters */delta_decode(
                enable_icc, icc_index[env], icc_index_prev,
                icc_dt[env], nr_icc_par,
                if (icc_mode == 0 || icc_mode == 3) 2 else 1,
                0, 7
            )

            /* delta modulo decode ipd parameters */delta_modulo_decode(
                enable_ipdopd, ipd_index[env], ipd_index_prev,
                ipd_dt[env], nr_ipdopd_par, 1, 7
            )

            /* delta modulo decode opd parameters */delta_modulo_decode(
                enable_ipdopd, opd_index[env], opd_index_prev,
                opd_dt[env], nr_ipdopd_par, 1, 7
            )
            env++
        }

        /* handle error case */if (num_env == 0) {
            /* force to 1 */
            num_env = 1
            if (enable_iid) {
                bin = 0
                while (bin < 34) {
                    iid_index[0][bin] = iid_index_prev[bin]
                    bin++
                }
            } else {
                bin = 0
                while (bin < 34) {
                    iid_index[0][bin] = 0
                    bin++
                }
            }
            if (enable_icc) {
                bin = 0
                while (bin < 34) {
                    icc_index[0][bin] = icc_index_prev[bin]
                    bin++
                }
            } else {
                bin = 0
                while (bin < 34) {
                    icc_index[0][bin] = 0
                    bin++
                }
            }
            if (enable_ipdopd) {
                bin = 0
                while (bin < 17) {
                    ipd_index[0][bin] = ipd_index_prev[bin]
                    opd_index[0][bin] = opd_index_prev[bin]
                    bin++
                }
            } else {
                bin = 0
                while (bin < 17) {
                    ipd_index[0][bin] = 0
                    opd_index[0][bin] = 0
                    bin++
                }
            }
        }

        /* update previous indices */bin = 0
        while (bin < 34) {
            iid_index_prev[bin] = iid_index[num_env - 1][bin]
            bin++
        }
        bin = 0
        while (bin < 34) {
            icc_index_prev[bin] = icc_index[num_env - 1][bin]
            bin++
        }
        bin = 0
        while (bin < 17) {
            ipd_index_prev[bin] = ipd_index[num_env - 1][bin]
            opd_index_prev[bin] = opd_index[num_env - 1][bin]
            bin++
        }
        ps_data_available = 0
        if (frame_class == 0) {
            border_position[0] = 0
            env = 1
            while (env < num_env) {
                border_position[env] = env * numTimeSlotsRate / num_env
                env++
            }
            border_position[num_env] = numTimeSlotsRate
        } else {
            border_position[0] = 0
            if (border_position[num_env] < numTimeSlotsRate) {
                bin = 0
                while (bin < 34) {
                    iid_index[num_env][bin] = iid_index[num_env - 1][bin]
                    icc_index[num_env][bin] = icc_index[num_env - 1][bin]
                    bin++
                }
                bin = 0
                while (bin < 17) {
                    ipd_index[num_env][bin] = ipd_index[num_env - 1][bin]
                    opd_index[num_env][bin] = opd_index[num_env - 1][bin]
                    bin++
                }
                num_env++
                border_position[num_env] = numTimeSlotsRate
            }
            env = 1
            while (env < num_env) {
                var thr = numTimeSlotsRate - (num_env - env)
                if (border_position[env] > thr) {
                    border_position[env] = thr
                } else {
                    thr = border_position[env - 1] + 1
                    if (border_position[env] < thr) {
                        border_position[env] = thr
                    }
                }
                env++
            }
        }

        /* make sure that the indices of all parameters can be mapped
		 * to the same hybrid synthesis filterbank
		 */if (use34hybrid_bands) {
            env = 0
            while (env < num_env) {
                if (iid_mode != 2 && iid_mode != 5) map20indexto34(iid_index[env], 34)
                if (icc_mode != 2 && icc_mode != 5) map20indexto34(icc_index[env], 34)
                if (ipd_mode != 2 && ipd_mode != 5) {
                    map20indexto34(ipd_index[env], 17)
                    map20indexto34(opd_index[env], 17)
                }
                env++
            }
        }
    }

    /* decorrelate the mono signal using an allpass filter */
    private fun ps_decorrelate(
        X_left: Array<Array<FloatArray>>, X_right: Array<Array<FloatArray>>,
        X_hybrid_left: Array<Array<FloatArray>>, X_hybrid_right: Array<Array<FloatArray>>
    ) {
        var gr: Int
        var n: Int
        var m: Int
        var bk: Int
        var temp_delay = 0
        var sb: Int
        var maxsb: Int
        val temp_delay_ser = IntArray(NO_ALLPASS_LINKS)
        var P_SmoothPeakDecayDiffNrg: Float
        var nrg: Float
        val P = Array(32) { FloatArray(34) }
        val G_TransientRatio = Array(32) { FloatArray(34) }
        val inputLeft = FloatArray(2)


        /* chose hybrid filterbank: 20 or 34 band case */
        val Phi_Fract_SubQmf: Array<FloatArray>
        if (use34hybrid_bands) {
            Phi_Fract_SubQmf = Phi_Fract_SubQmf34
        } else {
            Phi_Fract_SubQmf = Phi_Fract_SubQmf20
        }

        /* clear the energy values */n = 0
        while (n < 32) {
            bk = 0
            while (bk < 34) {
                P[n][bk] = 0f
                bk++
            }
            n++
        }

        /* calculate the energy in each parameter band b(k) */gr = 0
        while (gr < num_groups) {

            /* select the parameter index b(k) to which this group belongs */bk =
                NEGATE_IPD_MASK.inv() and map_group2bk[gr]

            /* select the upper subband border for this group */maxsb =
                if (gr < num_hybrid_groups) group_border[gr] + 1 else group_border[gr + 1]
            sb = group_border[gr]
            while (sb < maxsb) {
                n = border_position[0]
                while (n < border_position[num_env]) {


                    /* input from hybrid subbands or QMF subbands */if (gr < num_hybrid_groups) {
                        inputLeft[0] = X_hybrid_left[n][sb][0]
                        inputLeft[1] = X_hybrid_left[n][sb][1]
                    } else {
                        inputLeft[0] = X_left[n][sb][0]
                        inputLeft[1] = X_left[n][sb][1]
                    }

                    /* accumulate energy */P[n][bk] += inputLeft[0] * inputLeft[0] + inputLeft[1] * inputLeft[1]
                    n++
                }
                sb++
            }
            gr++
        }

        /* calculate transient reduction ratio for each parameter band b(k) */bk = 0
        while (bk < nr_par_bands) {
            n = border_position[0]
            while (n < border_position[num_env]) {
                val gamma = 1.5f
                P_PeakDecayNrg[bk] = P_PeakDecayNrg[bk] * alpha_decay
                if (P_PeakDecayNrg[bk] < P[n][bk]) P_PeakDecayNrg[bk] = P[n][bk]

                /* apply smoothing filter to peak decay energy */P_SmoothPeakDecayDiffNrg =
                    P_SmoothPeakDecayDiffNrg_prev[bk]
                P_SmoothPeakDecayDiffNrg += (P_PeakDecayNrg[bk] - P[n][bk] - P_SmoothPeakDecayDiffNrg_prev[bk]) * alpha_smooth
                P_SmoothPeakDecayDiffNrg_prev[bk] = P_SmoothPeakDecayDiffNrg

                /* apply smoothing filter to energy */nrg = P_prev[bk]
                nrg += (P[n][bk] - P_prev[bk]) * alpha_smooth
                P_prev[bk] = nrg

                /* calculate transient ratio */if (P_SmoothPeakDecayDiffNrg * gamma <= nrg) {
                    G_TransientRatio[n][bk] = 1.0f
                } else {
                    G_TransientRatio[n][bk] = nrg / (P_SmoothPeakDecayDiffNrg * gamma)
                }
                n++
            }
            bk++
        }

        /* apply stereo decorrelation filter to the signal */gr = 0
        while (gr < num_groups) {
            maxsb = if (gr < num_hybrid_groups) group_border[gr] + 1 else group_border[gr + 1]

            /* QMF channel */sb = group_border[gr]
            while (sb < maxsb) {
                var g_DecaySlope: Float
                val g_DecaySlope_filt = FloatArray(NO_ALLPASS_LINKS)

                /* g_DecaySlope: [0..1] */if (gr < num_hybrid_groups || sb <= decay_cutoff) {
                    g_DecaySlope = 1.0f
                } else {
                    val decay = decay_cutoff - sb
                    if (decay <= -20 /* -1/DECAY_SLOPE */) {
                        g_DecaySlope = 0f
                    } else {
                        /* decay(int)*decay_slope(frac) = g_DecaySlope(frac) */
                        g_DecaySlope = 1.0f + DECAY_SLOPE * decay
                    }
                }

                /* calculate g_DecaySlope_filt for every m multiplied by filter_a[m] */m = 0
                while (m < NO_ALLPASS_LINKS) {
                    g_DecaySlope_filt[m] = g_DecaySlope * filter_a.get(m)
                    m++
                }


                /* set delay indices */temp_delay = saved_delay
                n = 0
                while (n < NO_ALLPASS_LINKS) {
                    temp_delay_ser[n] = delay_buf_index_ser[n]
                    n++
                }
                n = border_position[0]
                while (n < border_position[num_env]) {
                    val tmp = FloatArray(2)
                    val tmp0 = FloatArray(2)
                    val R0 = FloatArray(2)
                    if (gr < num_hybrid_groups) {
                        /* hybrid filterbank input */
                        inputLeft[0] = X_hybrid_left[n][sb][0]
                        inputLeft[1] = X_hybrid_left[n][sb][1]
                    } else {
                        /* QMF filterbank input */
                        inputLeft[0] = X_left[n][sb][0]
                        inputLeft[1] = X_left[n][sb][1]
                    }
                    if (sb > nr_allpass_bands && gr >= num_hybrid_groups) {
                        /* delay */

                        /* never hybrid subbands here, always QMF subbands */
                        tmp[0] = delay_Qmf[delay_buf_index_delay[sb]][sb][0]
                        tmp[1] = delay_Qmf[delay_buf_index_delay[sb]][sb][1]
                        R0[0] = tmp[0]
                        R0[1] = tmp[1]
                        delay_Qmf[delay_buf_index_delay[sb]][sb][0] = inputLeft[0]
                        delay_Qmf[delay_buf_index_delay[sb]][sb][1] = inputLeft[1]
                    } else {
                        /* allpass filter */
                        //int m;
                        val Phi_Fract = FloatArray(2)

                        /* fetch parameters */if (gr < num_hybrid_groups) {
                            /* select data from the hybrid subbands */
                            tmp0[0] = delay_SubQmf[temp_delay][sb][0]
                            tmp0[1] = delay_SubQmf[temp_delay][sb][1]
                            delay_SubQmf[temp_delay][sb][0] = inputLeft[0]
                            delay_SubQmf[temp_delay][sb][1] = inputLeft[1]
                            Phi_Fract[0] = Phi_Fract_SubQmf[sb][0]
                            Phi_Fract[1] = Phi_Fract_SubQmf[sb][1]
                        } else {
                            /* select data from the QMF subbands */
                            tmp0[0] = delay_Qmf[temp_delay][sb][0]
                            tmp0[1] = delay_Qmf[temp_delay][sb][1]
                            delay_Qmf[temp_delay][sb][0] = inputLeft[0]
                            delay_Qmf[temp_delay][sb][1] = inputLeft[1]
                            Phi_Fract[0] = Phi_Fract_Qmf.get(sb).get(0)
                            Phi_Fract[1] = Phi_Fract_Qmf.get(sb).get(1)
                        }

                        /* z^(-2) * Phi_Fract[k] */tmp[0] = tmp[0] * Phi_Fract[0] + tmp0[1] * Phi_Fract[1]
                        tmp[1] = tmp0[1] * Phi_Fract[0] - tmp0[0] * Phi_Fract[1]
                        R0[0] = tmp[0]
                        R0[1] = tmp[1]
                        m = 0
                        while (m < NO_ALLPASS_LINKS) {
                            val Q_Fract_allpass = FloatArray(2)
                            val tmp2 = FloatArray(2)

                            /* fetch parameters */if (gr < num_hybrid_groups) {
                                /* select data from the hybrid subbands */
                                tmp0[0] = delay_SubQmf_ser[m][temp_delay_ser[m]][sb][0]
                                tmp0[1] = delay_SubQmf_ser[m][temp_delay_ser[m]][sb][1]
                                if (use34hybrid_bands) {
                                    Q_Fract_allpass[0] = Q_Fract_allpass_SubQmf34.get(sb).get(m).get(0)
                                    Q_Fract_allpass[1] = Q_Fract_allpass_SubQmf34.get(sb).get(m).get(1)
                                } else {
                                    Q_Fract_allpass[0] = Q_Fract_allpass_SubQmf20.get(sb).get(m).get(0)
                                    Q_Fract_allpass[1] = Q_Fract_allpass_SubQmf20.get(sb).get(m).get(1)
                                }
                            } else {
                                /* select data from the QMF subbands */
                                tmp0[0] = delay_Qmf_ser[m][temp_delay_ser[m]][sb][0]
                                tmp0[1] = delay_Qmf_ser[m][temp_delay_ser[m]][sb][1]
                                Q_Fract_allpass[0] = Q_Fract_allpass_Qmf.get(sb).get(m).get(0)
                                Q_Fract_allpass[1] = Q_Fract_allpass_Qmf.get(sb).get(m).get(1)
                            }

                            /* delay by a fraction */
                            /* z^(-d(m)) * Q_Fract_allpass[k,m] */tmp[0] =
                                tmp0[0] * Q_Fract_allpass[0] + tmp0[1] * Q_Fract_allpass[1]
                            tmp[1] = tmp0[1] * Q_Fract_allpass[0] - tmp0[0] * Q_Fract_allpass[1]

                            /* -a(m) * g_DecaySlope[k] */tmp[0] += -(g_DecaySlope_filt[m] * R0[0])
                            tmp[1] += -(g_DecaySlope_filt[m] * R0[1])

                            /* -a(m) * g_DecaySlope[k] * Q_Fract_allpass[k,m] * z^(-d(m)) */tmp2[0] =
                                R0[0] + g_DecaySlope_filt[m] * tmp[0]
                            tmp2[1] = R0[1] + g_DecaySlope_filt[m] * tmp[1]

                            /* store sample */if (gr < num_hybrid_groups) {
                                delay_SubQmf_ser[m][temp_delay_ser[m]][sb][0] = tmp2[0]
                                delay_SubQmf_ser[m][temp_delay_ser[m]][sb][1] = tmp2[1]
                            } else {
                                delay_Qmf_ser[m][temp_delay_ser[m]][sb][0] = tmp2[0]
                                delay_Qmf_ser[m][temp_delay_ser[m]][sb][1] = tmp2[1]
                            }

                            /* store for next iteration (or as output value if last iteration) */R0[0] = tmp[0]
                            R0[1] = tmp[1]
                            m++
                        }
                    }

                    /* select b(k) for reading the transient ratio */bk = NEGATE_IPD_MASK.inv() and map_group2bk[gr]

                    /* duck if a past transient is found */R0[0] = G_TransientRatio[n][bk] * R0[0]
                    R0[1] = G_TransientRatio[n][bk] * R0[1]
                    if (gr < num_hybrid_groups) {
                        /* hybrid */
                        X_hybrid_right[n][sb][0] = R0[0]
                        X_hybrid_right[n][sb][1] = R0[1]
                    } else {
                        /* QMF */
                        X_right[n][sb][0] = R0[0]
                        X_right[n][sb][1] = R0[1]
                    }

                    /* Update delay buffer index */if (++temp_delay >= 2) {
                        temp_delay = 0
                    }

                    /* update delay indices */if (sb > nr_allpass_bands && gr >= num_hybrid_groups) {
                        /* delay_D depends on the samplerate, it can hold the values 14 and 1 */
                        if (++delay_buf_index_delay[sb] >= delay_D[sb]) {
                            delay_buf_index_delay[sb] = 0
                        }
                    }
                    m = 0
                    while (m < NO_ALLPASS_LINKS) {
                        if (++temp_delay_ser[m] >= num_sample_delay_ser[m]) {
                            temp_delay_ser[m] = 0
                        }
                        m++
                    }
                    n++
                }
                sb++
            }
            gr++
        }

        /* update delay indices */saved_delay = temp_delay
        m = 0
        while (m < NO_ALLPASS_LINKS) {
            delay_buf_index_ser[m] = temp_delay_ser[m]
            m++
        }
    }

    private fun magnitude_c(c: FloatArray): Float {
        return sqrt((c[0] * c[0] + c[1] * c[1]).toDouble()).toFloat()
    }

    private fun ps_mix_phase(
        X_left: Array<Array<FloatArray>>, X_right: Array<Array<FloatArray>>,
        X_hybrid_left: Array<Array<FloatArray>>, X_hybrid_right: Array<Array<FloatArray>>
    ) {
        var n: Int
        var gr: Int
        var bk = 0
        var sb: Int
        var maxsb: Int
        var env: Int
        val nr_ipdopd_par: Int
        val h11 = FloatArray(2)
        val h12 = FloatArray(2)
        val h21 = FloatArray(2)
        val h22 = FloatArray(2)
        val H11 = FloatArray(2)
        val H12 = FloatArray(2)
        val H21 = FloatArray(2)
        val H22 = FloatArray(2)
        val deltaH11 = FloatArray(2)
        val deltaH12 = FloatArray(2)
        val deltaH21 = FloatArray(2)
        val deltaH22 = FloatArray(2)
        val tempLeft = FloatArray(2)
        val tempRight = FloatArray(2)
        val phaseLeft = FloatArray(2)
        val phaseRight = FloatArray(2)
        var L: Float
        val sf_iid: FloatArray
        val no_iid_steps: Int
        if (iid_mode >= 3) {
            no_iid_steps = 15
            sf_iid = sf_iid_fine
        } else {
            no_iid_steps = 7
            sf_iid = sf_iid_normal
        }
        nr_ipdopd_par = if (ipd_mode == 0 || ipd_mode == 3) {
            11 /* resolution */
        } else {
            this.nr_ipdopd_par
        }
        gr = 0
        while (gr < num_groups) {
            bk = NEGATE_IPD_MASK.inv() and map_group2bk[gr]

            /* use one channel per group in the subqmf domain */maxsb =
                if (gr < num_hybrid_groups) group_border[gr] + 1 else group_border[gr + 1]
            env = 0
            while (env < num_env) {
                if (icc_mode < 3) {
                    /* type 'A' mixing as described in 8.6.4.6.2.1 */
                    var c_1: Float
                    var c_2: Float
                    var cosa: Float
                    var sina: Float
                    var cosb: Float
                    var sinb: Float
                    var ab1: Float
                    var ab2: Float
                    var ab3: Float
                    var ab4: Float

                    /*
					 c_1 = sqrt(2.0 / (1.0 + pow(10.0, quant_iid[no_iid_steps + iid_index] / 10.0)));
					 c_2 = sqrt(2.0 / (1.0 + pow(10.0, quant_iid[no_iid_steps - iid_index] / 10.0)));
					 alpha = 0.5 * acos(quant_rho[icc_index]);
					 beta = alpha * ( c_1 - c_2 ) / sqrt(2.0);
					 */
                    //printf("%d\n", ps.iid_index[env][bk]);

                    /* calculate the scalefactors c_1 and c_2 from the intensity differences */c_1 =
                        sf_iid[no_iid_steps + iid_index[env][bk]]
                    c_2 = sf_iid[no_iid_steps - iid_index[env][bk]]

                    /* calculate alpha and beta using the ICC parameters */cosa = cos_alphas.get(icc_index[env][bk])
                    sina = sin_alphas.get(icc_index[env][bk])
                    if (iid_mode >= 3) {
                        if (iid_index[env][bk] < 0) {
                            cosb = cos_betas_fine.get(-iid_index[env][bk]).get(icc_index[env][bk])
                            sinb = -sin_betas_fine.get(-iid_index[env][bk]).get(icc_index[env][bk])
                        } else {
                            cosb = cos_betas_fine.get(iid_index[env][bk]).get(icc_index[env][bk])
                            sinb = sin_betas_fine.get(iid_index[env][bk]).get(icc_index[env][bk])
                        }
                    } else {
                        if (iid_index[env][bk] < 0) {
                            cosb = cos_betas_normal.get(-iid_index[env][bk]).get(icc_index[env][bk])
                            sinb = -sin_betas_normal.get(-iid_index[env][bk]).get(icc_index[env][bk])
                        } else {
                            cosb = cos_betas_normal.get(iid_index[env][bk]).get(icc_index[env][bk])
                            sinb = sin_betas_normal.get(iid_index[env][bk]).get(icc_index[env][bk])
                        }
                    }
                    ab1 = cosb * cosa
                    ab2 = sinb * sina
                    ab3 = sinb * cosa
                    ab4 = cosb * sina

                    /* h_xy: COEF */h11[0] = c_2 * (ab1 - ab2)
                    h12[0] = c_1 * (ab1 + ab2)
                    h21[0] = c_2 * (ab3 + ab4)
                    h22[0] = c_1 * (ab3 - ab4)
                } else {
                    /* type 'B' mixing as described in 8.6.4.6.2.2 */
                    var sina: Float
                    var cosa: Float
                    var cosg: Float
                    var sing: Float
                    if (iid_mode >= 3) {
                        val abs_iid: Int = abs(iid_index[env][bk])
                        cosa = sincos_alphas_B_fine.get(no_iid_steps + iid_index[env][bk]).get(icc_index[env][bk])
                        sina =
                            sincos_alphas_B_fine.get(30 - (no_iid_steps + iid_index[env][bk])).get(icc_index[env][bk])
                        cosg = cos_gammas_fine.get(abs_iid).get(icc_index[env][bk])
                        sing = sin_gammas_fine.get(abs_iid).get(icc_index[env][bk])
                    } else {
                        val abs_iid: Int = abs(iid_index[env][bk])
                        cosa = sincos_alphas_B_normal.get(no_iid_steps + iid_index[env][bk]).get(icc_index[env][bk])
                        sina =
                            sincos_alphas_B_normal.get(14 - (no_iid_steps + iid_index[env][bk])).get(icc_index[env][bk])
                        cosg = cos_gammas_normal.get(abs_iid).get(icc_index[env][bk])
                        sing = sin_gammas_normal.get(abs_iid).get(icc_index[env][bk])
                    }
                    h11[0] = COEF_SQRT2 * (cosa * cosg)
                    h12[0] = COEF_SQRT2 * (sina * cosg)
                    h21[0] = COEF_SQRT2 * (-cosa * sing)
                    h22[0] = COEF_SQRT2 * (sina * sing)
                }

                /* calculate phase rotation parameters H_xy */
                /* note that the imaginary part of these parameters are only calculated when
				 IPD and OPD are enabled
				 */if (enable_ipdopd && bk < nr_ipdopd_par) {
                    var xy: Float
                    var pq: Float
                    var xypq: Float

                    /* ringbuffer index */
                    var i = phase_hist

                    /* previous value */tempLeft[0] = ipd_prev[bk][i][0] * 0.25f
                    tempLeft[1] = ipd_prev[bk][i][1] * 0.25f
                    tempRight[0] = opd_prev[bk][i][0] * 0.25f
                    tempRight[1] = opd_prev[bk][i][1] * 0.25f

                    /* save current value */ipd_prev[bk][i][0] =
                        ipdopd_cos_tab.get(abs(ipd_index[env][bk]))
                    ipd_prev[bk][i][1] = ipdopd_sin_tab.get(abs(ipd_index[env][bk]))
                    opd_prev[bk][i][0] = ipdopd_cos_tab.get(abs(opd_index[env][bk]))
                    opd_prev[bk][i][1] = ipdopd_sin_tab.get(abs(opd_index[env][bk]))

                    /* add current value */tempLeft[0] += ipd_prev[bk][i][0]
                    tempLeft[1] += ipd_prev[bk][i][1]
                    tempRight[0] += opd_prev[bk][i][0]
                    tempRight[1] += opd_prev[bk][i][1]

                    /* ringbuffer index */if (i == 0) {
                        i = 2
                    }
                    i--

                    /* get value before previous */tempLeft[0] += ipd_prev[bk][i][0] * 0.5f
                    tempLeft[1] += ipd_prev[bk][i][1] * 0.5f
                    tempRight[0] += opd_prev[bk][i][0] * 0.5f
                    tempRight[1] += opd_prev[bk][i][1] * 0.5f
                    xy = magnitude_c(tempRight)
                    pq = magnitude_c(tempLeft)
                    if (xy != 0f) {
                        phaseLeft[0] = tempRight[0] / xy
                        phaseLeft[1] = tempRight[1] / xy
                    } else {
                        phaseLeft[0] = 0f
                        phaseLeft[1] = 0f
                    }
                    xypq = xy * pq
                    if (xypq != 0f) {
                        val tmp1 = tempRight[0] * tempLeft[0] + tempRight[1] * tempLeft[1]
                        val tmp2 = tempRight[1] * tempLeft[0] - tempRight[0] * tempLeft[1]
                        phaseRight[0] = tmp1 / xypq
                        phaseRight[1] = tmp2 / xypq
                    } else {
                        phaseRight[0] = 0f
                        phaseRight[1] = 0f
                    }

                    /* MUL_F(COEF, REAL) = COEF */h11[1] = h11[0] * phaseLeft[1]
                    h12[1] = h12[0] * phaseRight[1]
                    h21[1] = h21[0] * phaseLeft[1]
                    h22[1] = h22[0] * phaseRight[1]
                    h11[0] = h11[0] * phaseLeft[0]
                    h12[0] = h12[0] * phaseRight[0]
                    h21[0] = h21[0] * phaseLeft[0]
                    h22[0] = h22[0] * phaseRight[0]
                }

                /* length of the envelope n_e+1 - n_e (in time samples) */
                /* 0 < L <= 32: integer */L = (border_position[env + 1] - border_position[env]).toFloat()

                /* obtain final H_xy by means of linear interpolation */deltaH11[0] = (h11[0] - h11_prev[gr][0]) / L
                deltaH12[0] = (h12[0] - h12_prev[gr][0]) / L
                deltaH21[0] = (h21[0] - h21_prev[gr][0]) / L
                deltaH22[0] = (h22[0] - h22_prev[gr][0]) / L
                H11[0] = h11_prev[gr][0]
                H12[0] = h12_prev[gr][0]
                H21[0] = h21_prev[gr][0]
                H22[0] = h22_prev[gr][0]
                h11_prev[gr][0] = h11[0]
                h12_prev[gr][0] = h12[0]
                h21_prev[gr][0] = h21[0]
                h22_prev[gr][0] = h22[0]

                /* only calculate imaginary part when needed */if (enable_ipdopd && bk < nr_ipdopd_par) {
                    /* obtain final H_xy by means of linear interpolation */
                    deltaH11[1] = (h11[1] - h11_prev[gr][1]) / L
                    deltaH12[1] = (h12[1] - h12_prev[gr][1]) / L
                    deltaH21[1] = (h21[1] - h21_prev[gr][1]) / L
                    deltaH22[1] = (h22[1] - h22_prev[gr][1]) / L
                    H11[1] = h11_prev[gr][1]
                    H12[1] = h12_prev[gr][1]
                    H21[1] = h21_prev[gr][1]
                    H22[1] = h22_prev[gr][1]
                    if (NEGATE_IPD_MASK and map_group2bk[gr] !== 0) {
                        deltaH11[1] = -deltaH11[1]
                        deltaH12[1] = -deltaH12[1]
                        deltaH21[1] = -deltaH21[1]
                        deltaH22[1] = -deltaH22[1]
                        H11[1] = -H11[1]
                        H12[1] = -H12[1]
                        H21[1] = -H21[1]
                        H22[1] = -H22[1]
                    }
                    h11_prev[gr][1] = h11[1]
                    h12_prev[gr][1] = h12[1]
                    h21_prev[gr][1] = h21[1]
                    h22_prev[gr][1] = h22[1]
                }

                /* apply H_xy to the current envelope band of the decorrelated subband */n = border_position[env]
                while (n < border_position[env + 1]) {

                    /* addition finalises the interpolation over every n */H11[0] += deltaH11[0]
                    H12[0] += deltaH12[0]
                    H21[0] += deltaH21[0]
                    H22[0] += deltaH22[0]
                    if (enable_ipdopd && bk < nr_ipdopd_par) {
                        H11[1] += deltaH11[1]
                        H12[1] += deltaH12[1]
                        H21[1] += deltaH21[1]
                        H22[1] += deltaH22[1]
                    }

                    /* channel is an alias to the subband */sb = group_border[gr]
                    while (sb < maxsb) {
                        val inLeft = FloatArray(2)
                        val inRight = FloatArray(2)

                        /* load decorrelated samples */if (gr < num_hybrid_groups) {
                            inLeft[0] = X_hybrid_left[n][sb][0]
                            inLeft[1] = X_hybrid_left[n][sb][1]
                            inRight[0] = X_hybrid_right[n][sb][0]
                            inRight[1] = X_hybrid_right[n][sb][1]
                        } else {
                            inLeft[0] = X_left[n][sb][0]
                            inLeft[1] = X_left[n][sb][1]
                            inRight[0] = X_right[n][sb][0]
                            inRight[1] = X_right[n][sb][1]
                        }

                        /* apply mixing */tempLeft[0] = H11[0] * inLeft[0] + H21[0] * inRight[0]
                        tempLeft[1] = H11[0] * inLeft[1] + H21[0] * inRight[1]
                        tempRight[0] = H12[0] * inLeft[0] + H22[0] * inRight[0]
                        tempRight[1] = H12[0] * inLeft[1] + H22[0] * inRight[1]

                        /* only perform imaginary operations when needed */if (enable_ipdopd && bk < nr_ipdopd_par) {
                            /* apply rotation */
                            tempLeft[0] -= H11[1] * inLeft[1] + H21[1] * inRight[1]
                            tempLeft[1] += H11[1] * inLeft[0] + H21[1] * inRight[0]
                            tempRight[0] -= H12[1] * inLeft[1] + H22[1] * inRight[1]
                            tempRight[1] += H12[1] * inLeft[0] + H22[1] * inRight[0]
                        }

                        /* store final samples */if (gr < num_hybrid_groups) {
                            X_hybrid_left[n][sb][0] = tempLeft[0]
                            X_hybrid_left[n][sb][1] = tempLeft[1]
                            X_hybrid_right[n][sb][0] = tempRight[0]
                            X_hybrid_right[n][sb][1] = tempRight[1]
                        } else {
                            X_left[n][sb][0] = tempLeft[0]
                            X_left[n][sb][1] = tempLeft[1]
                            X_right[n][sb][0] = tempRight[0]
                            X_right[n][sb][1] = tempRight[1]
                        }
                        sb++
                    }
                    n++
                }

                /* shift phase smoother's circular buffer index */phase_hist++
                if (phase_hist == 2) {
                    phase_hist = 0
                }
                env++
            }
            gr++
        }
    }

    /* main Parametric Stereo decoding function */
    fun process(X_left: Array<Array<FloatArray>>, X_right: Array<Array<FloatArray>>): Int {
        val X_hybrid_left = Array(32) { Array(32) { FloatArray(2) } }
        val X_hybrid_right = Array(32) { Array(32) { FloatArray(2) } }

        /* delta decoding of the bitstream data */ps_data_decode()

        /* set up some parameters depending on filterbank type */if (use34hybrid_bands) {
            group_border = group_border34
            map_group2bk = map_group2bk34
            num_groups = 32 + 18
            num_hybrid_groups = 32
            nr_par_bands = 34
            decay_cutoff = 5
        } else {
            group_border = group_border20
            map_group2bk = map_group2bk20
            num_groups = 10 + 12
            num_hybrid_groups = 10
            nr_par_bands = 20
            decay_cutoff = 3
        }

        /* Perform further analysis on the lowest subbands to get a higher
		 * frequency resolution
		 */hyb.hybrid_analysis(
            X_left, X_hybrid_left,
            use34hybrid_bands, numTimeSlotsRate
        )

        /* decorrelate mono signal */ps_decorrelate(X_left, X_right, X_hybrid_left, X_hybrid_right)

        /* apply mixing and phase parameters */ps_mix_phase(X_left, X_right, X_hybrid_left, X_hybrid_right)

        /* hybrid synthesis, to rebuild the SBR QMF matrices */hyb.hybrid_synthesis(
            X_left, X_hybrid_left,
            use34hybrid_bands, numTimeSlotsRate
        )
        hyb.hybrid_synthesis(
            X_right, X_hybrid_right,
            use34hybrid_bands, numTimeSlotsRate
        )
        return 0
    }
}
