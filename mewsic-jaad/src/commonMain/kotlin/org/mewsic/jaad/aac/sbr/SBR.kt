package org.mewsic.jaad.aac.sbr

import org.mewsic.jaad.aac.AACException
import org.mewsic.jaad.aac.SampleFrequency
import org.mewsic.jaad.aac.ps.PS
import org.mewsic.jaad.aac.sbr.Constants.Companion.EXTENSION_ID_PS
import org.mewsic.jaad.aac.sbr.Constants.Companion.EXT_SBR_DATA_CRC
import org.mewsic.jaad.aac.sbr.Constants.Companion.FIXFIX
import org.mewsic.jaad.aac.sbr.Constants.Companion.FIXVAR
import org.mewsic.jaad.aac.sbr.Constants.Companion.MAX_L_E
import org.mewsic.jaad.aac.sbr.Constants.Companion.MAX_M
import org.mewsic.jaad.aac.sbr.Constants.Companion.MAX_NTSR
import org.mewsic.jaad.aac.sbr.Constants.Companion.MAX_NTSRHFG
import org.mewsic.jaad.aac.sbr.Constants.Companion.NO_TIME_SLOTS
import org.mewsic.jaad.aac.sbr.Constants.Companion.NO_TIME_SLOTS_960
import org.mewsic.jaad.aac.sbr.Constants.Companion.RATE
import org.mewsic.jaad.aac.sbr.Constants.Companion.T_HFADJ
import org.mewsic.jaad.aac.sbr.Constants.Companion.T_HFGEN
import org.mewsic.jaad.aac.sbr.Constants.Companion.VARFIX
import org.mewsic.jaad.aac.sbr.Constants.Companion.VARVAR
import org.mewsic.jaad.aac.sbr.HuffmanTables.Companion.F_HUFFMAN_ENV_1_5DB
import org.mewsic.jaad.aac.sbr.HuffmanTables.Companion.F_HUFFMAN_ENV_3_0DB
import org.mewsic.jaad.aac.sbr.HuffmanTables.Companion.F_HUFFMAN_ENV_BAL_1_5DB
import org.mewsic.jaad.aac.sbr.HuffmanTables.Companion.F_HUFFMAN_ENV_BAL_3_0DB
import org.mewsic.jaad.aac.sbr.HuffmanTables.Companion.T_HUFFMAN_ENV_1_5DB
import org.mewsic.jaad.aac.sbr.HuffmanTables.Companion.T_HUFFMAN_ENV_3_0DB
import org.mewsic.jaad.aac.sbr.HuffmanTables.Companion.T_HUFFMAN_ENV_BAL_1_5DB
import org.mewsic.jaad.aac.sbr.HuffmanTables.Companion.T_HUFFMAN_ENV_BAL_3_0DB
import org.mewsic.jaad.aac.sbr.HuffmanTables.Companion.T_HUFFMAN_NOISE_3_0DB
import org.mewsic.jaad.aac.sbr.HuffmanTables.Companion.T_HUFFMAN_NOISE_BAL_3_0DB
import org.mewsic.jaad.aac.syntax.BitStream
import org.mewsic.jaad.aac.syntax.Constants
import kotlin.math.max
import kotlin.math.min

class SBR(
    smallFrames: Boolean,
    var stereo: Boolean,
    val sample_rate: SampleFrequency,
    private val downSampledSBR: Boolean
) : org.mewsic.jaad.aac.sbr.Constants, Constants, HuffmanTables {
    var maxAACLine = 0
    var rate = 0
    var just_seeked = false
    var ret = 0
    var amp_res = BooleanArray(2)
    var k0 = 0
    var kx = 0
    var M = 0
    var N_master = 0
    var N_high = 0
    var N_low = 0
    var N_Q = 0
    var N_L = IntArray(4)
    var n = IntArray(2)
    var f_master = IntArray(64)
    var f_table_res = Array(2) { IntArray(64) }
    var f_table_noise = IntArray(64)
    var f_table_lim = Array(4) { IntArray(64) }
    var table_map_k_to_g = IntArray(64)
    var abs_bord_lead = IntArray(2)
    var abs_bord_trail = IntArray(2)
    var n_rel_lead = IntArray(2)
    var n_rel_trail = IntArray(2)
    var L_E = IntArray(2)
    var L_E_prev = IntArray(2)
    var L_Q = IntArray(2)
    var t_E = Array(2) { IntArray(MAX_L_E + 1) }
    var t_Q = Array(2) { IntArray(3) }
    var f = Array(2) { IntArray(MAX_L_E + 1) }
    var f_prev = IntArray(2)
    var G_temp_prev = Array(2) { Array<FloatArray?>(5) { FloatArray(64) } }
    var Q_temp_prev = Array(2) { Array<FloatArray?>(5) { FloatArray(64) } }
    var GQ_ringbuf_index = IntArray(2)
    var E = Array(2) { Array(64) { IntArray(MAX_L_E) } }
    var E_prev = Array(2) { IntArray(64) }
    var E_orig = Array(2) { Array(64) { FloatArray(MAX_L_E) } }
    var E_curr = Array(2) { Array(64) { FloatArray(MAX_L_E) } }
    var Q = Array(2) { Array(64) { IntArray(2) } }
    var Q_div = Array(2) { Array(64) { FloatArray(2) } }
    var Q_div2 = Array(2) { Array(64) { FloatArray(2) } }
    var Q_prev = Array(2) { IntArray(64) }
    var l_A = IntArray(2)
    var l_A_prev = IntArray(2)
    var bs_invf_mode = Array(2) { IntArray(MAX_L_E) }
    var bs_invf_mode_prev = Array(2) { IntArray(MAX_L_E) }
    var bwArray = Array(2) { FloatArray(64) }
    var bwArray_prev = Array(2) { FloatArray(64) }
    var noPatches = 0
    var patchNoSubbands = IntArray(64)
    var patchStartSubband = IntArray(64)
    var bs_add_harmonic = Array(2) { IntArray(64) }
    var bs_add_harmonic_prev = Array(2) { IntArray(64) }
    var index_noise_prev = IntArray(2)
    var psi_is_prev = IntArray(2)
    var bs_start_freq_prev: Int
    var bs_stop_freq_prev = 0
    var bs_xover_band_prev = 0
    var bs_freq_scale_prev = 0
    var bs_alter_scale_prev = false
    var bs_noise_bands_prev = 0
    var prevEnvIsShort = IntArray(2)
    var kx_prev = 0
    var bsco: Int
    var bsco_prev: Int
    var M_prev: Int
    var Reset: Boolean
    var frame = 0
    var header_count: Int
    var qmfa: Array<AnalysisFilterbank?> =
        arrayOfNulls<AnalysisFilterbank>(2)
    var qmfs: Array<SynthesisFilterbank?> =
        arrayOfNulls<SynthesisFilterbank>(2)
    var Xsbr = Array(2) { Array(MAX_NTSRHFG) { Array(64) { FloatArray(2) } } }
    var numTimeSlotsRate = 0
    var numTimeSlots = 0
    var tHFGen: Int
    var tHFAdj: Int
    var ps: PS? = null
    var isPSUsed = false
    var psResetFlag = false

    /* to get it compiling */ /* we'll see during the coding of all the tools, whether
	 these are all used or not.
	 */
    var bs_header_flag = false
    var bs_crc_flag = 0
    var bs_sbr_crc_bits = 0
    var bs_protocol_version = 0
    var bs_amp_res = true
    var bs_start_freq = 5
    var bs_stop_freq = 0
    var bs_xover_band = 0
    var bs_freq_scale = 2
    var bs_alter_scale = true
    var bs_noise_bands = 2
    var bs_limiter_bands = 2
    var bs_limiter_gains = 2
    var bs_interpol_freq = true
    var bs_smoothing_mode = true
    var bs_samplerate_mode = 1
    var bs_add_harmonic_flag = BooleanArray(2)
    var bs_add_harmonic_flag_prev = BooleanArray(2)
    var bs_extended_data = false
    var bs_extension_id = 0
    var bs_extension_data = 0
    var bs_coupling = false
    var bs_frame_class = IntArray(2)
    var bs_rel_bord = Array(2) { IntArray(9) }
    var bs_rel_bord_0 = Array(2) { IntArray(9) }
    var bs_rel_bord_1 = Array(2) { IntArray(9) }
    var bs_pointer = IntArray(2)
    var bs_abs_bord_0 = IntArray(2)
    var bs_abs_bord_1 = IntArray(2)
    var bs_num_rel_0 = IntArray(2)
    var bs_num_rel_1 = IntArray(2)
    var bs_df_env = Array(2) { IntArray(9) }
    var bs_df_noise = Array(2) { IntArray(3) }

    init {
        prevEnvIsShort[0] = -1
        prevEnvIsShort[1] = -1
        header_count = 0
        Reset = true
        tHFGen = T_HFGEN
        tHFAdj = T_HFADJ
        bsco = 0
        bsco_prev = 0
        M_prev = 0

        /* force sbr reset */bs_start_freq_prev = -1
        if (smallFrames) {
            numTimeSlotsRate = RATE * NO_TIME_SLOTS_960
            numTimeSlots = NO_TIME_SLOTS_960
        } else {
            numTimeSlotsRate = RATE * NO_TIME_SLOTS
            numTimeSlots = NO_TIME_SLOTS
        }
        GQ_ringbuf_index[0] = 0
        GQ_ringbuf_index[1] = 0
        if (stereo) {
            /* stereo */
            var j: Int
            qmfa[0] = AnalysisFilterbank(32)
            qmfa[1] = AnalysisFilterbank(32)
            qmfs[0] = SynthesisFilterbank(if (downSampledSBR) 32 else 64)
            qmfs[1] = SynthesisFilterbank(if (downSampledSBR) 32 else 64)
        } else {
            /* mono */
            qmfa[0] = AnalysisFilterbank(32)
            qmfs[0] = SynthesisFilterbank(if (downSampledSBR) 32 else 64)
            qmfs[1] = null
        }
    }

    fun sbrReset() {
        var j: Int
        if (qmfa[0] != null) qmfa[0]!!.reset()
        if (qmfa[1] != null) qmfa[1]!!.reset()
        if (qmfs[0] != null) qmfs[0]!!.reset()
        if (qmfs[1] != null) qmfs[1]!!.reset()
        j = 0
        while (j < 5) {
            if (G_temp_prev[0][j] != null) G_temp_prev[0][j]!!.fill(0f)
            if (G_temp_prev[1][j] != null) G_temp_prev[1][j]!!.fill(0f)
            if (Q_temp_prev[0][j] != null) Q_temp_prev[0][j]!!.fill(0f)
            if (Q_temp_prev[1][j] != null) Q_temp_prev[1][j]!!.fill(0f)
            j++
        }
        for (i in 0..39) {
            for (k in 0..63) {
                Xsbr[0][i][j][0] = 0f
                Xsbr[0][i][j][1] = 0f
                Xsbr[1][i][j][0] = 0f
                Xsbr[1][i][j][1] = 0f
            }
        }
        GQ_ringbuf_index[0] = 0
        GQ_ringbuf_index[1] = 0
        header_count = 0
        Reset = true
        L_E_prev[0] = 0
        L_E_prev[1] = 0
        bs_freq_scale = 2
        bs_alter_scale = true
        bs_noise_bands = 2
        bs_limiter_bands = 2
        bs_limiter_gains = 2
        bs_interpol_freq = true
        bs_smoothing_mode = true
        bs_start_freq = 5
        bs_amp_res = true
        bs_samplerate_mode = 1
        prevEnvIsShort[0] = -1
        prevEnvIsShort[1] = -1
        bsco = 0
        bsco_prev = 0
        M_prev = 0
        bs_start_freq_prev = -1
        f_prev[0] = 0
        f_prev[1] = 0
        j = 0
        while (j < MAX_M) {
            E_prev[0][j] = 0
            Q_prev[0][j] = 0
            E_prev[1][j] = 0
            Q_prev[1][j] = 0
            bs_add_harmonic_prev[0][j] = 0
            bs_add_harmonic_prev[1][j] = 0
            j++
        }
        bs_add_harmonic_flag_prev[0] = false
        bs_add_harmonic_flag_prev[1] = false
    }

    fun sbr_reset() {

        /* if these are different from the previous frame: Reset = 1 */
        Reset =
            bs_start_freq != bs_start_freq_prev || bs_stop_freq != bs_stop_freq_prev || bs_freq_scale != bs_freq_scale_prev || bs_alter_scale != bs_alter_scale_prev || bs_xover_band != bs_xover_band_prev || bs_noise_bands != bs_noise_bands_prev
        bs_start_freq_prev = bs_start_freq
        bs_stop_freq_prev = bs_stop_freq
        bs_freq_scale_prev = bs_freq_scale
        bs_alter_scale_prev = bs_alter_scale
        bs_xover_band_prev = bs_xover_band
        bs_noise_bands_prev = bs_noise_bands
    }

    fun calc_sbr_tables(
        start_freq: Int, stop_freq: Int,
        samplerate_mode: Int, freq_scale: Int,
        alter_scale: Boolean, xover_band: Int
    ): Int {
        var result = 0
        val k2: Int

        /* calculate the Master Frequency Table */k0 =
            FBT.qmf_start_channel(start_freq, samplerate_mode, sample_rate)
        k2 = FBT.qmf_stop_channel(stop_freq, sample_rate, k0)

        /* check k0 and k2 */if (sample_rate.frequency >= 48000) {
            if (k2 - k0 > 32) result += 1
        } else if (sample_rate.frequency <= 32000) {
            if (k2 - k0 > 48) result += 1
        } else { /* (sbr.sample_rate == 44100) */
            if (k2 - k0 > 45) result += 1
        }
        if (freq_scale == 0) {
            result += FBT.master_frequency_table_fs0(this, k0, k2, alter_scale)
        } else {
            result += FBT.master_frequency_table(this, k0, k2, freq_scale, alter_scale)
        }
        result += FBT.derived_frequency_table(this, xover_band, k2)
        result = if (result > 0) 1 else 0
        return result
    }

    /* table 2 */
    @Throws(AACException::class)
    fun decode(ld: BitStream, cnt: Int): Int {
        var result = 0
        var num_align_bits = 0
        val num_sbr_bits1 = ld.position.toLong()
        val num_sbr_bits2: Int
        val saved_start_freq: Int
        val saved_samplerate_mode: Int
        val saved_stop_freq: Int
        val saved_freq_scale: Int
        val saved_xover_band: Int
        val saved_alter_scale: Boolean
        val bs_extension_type = ld.readBits(4)
        if (bs_extension_type == EXT_SBR_DATA_CRC) {
            bs_sbr_crc_bits = ld.readBits(10)
        }

        /* save old header values, in case the new ones are corrupted */saved_start_freq = bs_start_freq
        saved_samplerate_mode = bs_samplerate_mode
        saved_stop_freq = bs_stop_freq
        saved_freq_scale = bs_freq_scale
        saved_alter_scale = bs_alter_scale
        saved_xover_band = bs_xover_band
        bs_header_flag = ld.readBool()
        if (bs_header_flag) sbr_header(ld)

        /* Reset? */sbr_reset()

        /* first frame should have a header */
        //if (!(sbr.frame == 0 && sbr.bs_header_flag == 0))
        if (header_count != 0) {
            if (Reset || bs_header_flag && just_seeked) {
                val rt = calc_sbr_tables(
                    bs_start_freq, bs_stop_freq,
                    bs_samplerate_mode, bs_freq_scale,
                    bs_alter_scale, bs_xover_band
                )

                /* if an error occured with the new header values revert to the old ones */if (rt > 0) {
                    calc_sbr_tables(
                        saved_start_freq, saved_stop_freq,
                        saved_samplerate_mode, saved_freq_scale,
                        saved_alter_scale, saved_xover_band
                    )
                }
            }
            if (result == 0) {
                result = sbr_data(ld)

                /* sbr_data() returning an error means that there was an error in
				 envelope_time_border_vector().
				 In this case the old time border vector is saved and all the previous
				 data normally read after sbr_grid() is saved.
				 */
                /* to be on the safe side, calculate old sbr tables in case of error */if (result > 0
                    && (Reset || bs_header_flag && just_seeked)
                ) {
                    calc_sbr_tables(
                        saved_start_freq, saved_stop_freq,
                        saved_samplerate_mode, saved_freq_scale,
                        saved_alter_scale, saved_xover_band
                    )
                }

                /* we should be able to safely set result to 0 now, */
                /* but practise indicates this doesn't work well */
            }
        } else {
            result = 1
        }
        num_sbr_bits2 = (ld.position - num_sbr_bits1).toInt()

        /* check if we read more bits then were available for sbr */if (8 * cnt < num_sbr_bits2) {
            throw AACException("frame overread")
            //faad_resetbits(ld, num_sbr_bits1+8*cnt);
            //num_sbr_bits2 = 8*cnt;

            /* turn off PS for the unfortunate case that we randomly read some
			 * PS data that looks correct */
            //this.ps_used = 0;

            /* Make sure it doesn't decode SBR in this frame, or we'll get glitches */
            //return 1;
        }
        run {

            /* -4 does not apply, bs_extension_type is re-read in this function */num_align_bits =
            8 * cnt /*- 4*/ - num_sbr_bits2
            while (num_align_bits > 7) {
                ld.readBits(8)
                num_align_bits -= 8
            }
            ld.readBits(num_align_bits)
        }
        return result
    }

    /* table 3 */
    @Throws(AACException::class)
    private fun sbr_header(ld: BitStream) {
        val bs_header_extra_1: Boolean
        val bs_header_extra_2: Boolean
        header_count++
        bs_amp_res = ld.readBool()

        /* bs_start_freq and bs_stop_freq must define a fequency band that does
		 not exceed 48 channels */bs_start_freq = ld.readBits(4)
        bs_stop_freq = ld.readBits(4)
        bs_xover_band = ld.readBits(3)
        ld.readBits(2) //reserved
        bs_header_extra_1 = ld.readBool()
        bs_header_extra_2 = ld.readBool()
        if (bs_header_extra_1) {
            bs_freq_scale = ld.readBits(2)
            bs_alter_scale = ld.readBool()
            bs_noise_bands = ld.readBits(2)
        } else {
            /* Default values */
            bs_freq_scale = 2
            bs_alter_scale = true
            bs_noise_bands = 2
        }
        if (bs_header_extra_2) {
            bs_limiter_bands = ld.readBits(2)
            bs_limiter_gains = ld.readBits(2)
            bs_interpol_freq = ld.readBool()
            bs_smoothing_mode = ld.readBool()
        } else {
            /* Default values */
            bs_limiter_bands = 2
            bs_limiter_gains = 2
            bs_interpol_freq = true
            bs_smoothing_mode = true
        }
    }

    /* table 4 */
    @Throws(AACException::class)
    private fun sbr_data(ld: BitStream): Int {
        var result: Int
        rate = if (bs_samplerate_mode != 0) 2 else 1
        if (stereo) {
            if (sbr_channel_pair_element(ld).also { result = it } > 0) return result
        } else {
            if (sbr_single_channel_element(ld).also { result = it } > 0) return result
        }
        return 0
    }

    /* table 5 */
    @Throws(AACException::class)
    private fun sbr_single_channel_element(ld: BitStream): Int {
        var result: Int
        if (ld.readBool()) {
            ld.readBits(4) //reserved
        }
        if (sbr_grid(ld, 0).also { result = it } > 0) return result
        sbr_dtdf(ld, 0)
        invf_mode(ld, 0)
        sbr_envelope(ld, 0)
        sbr_noise(ld, 0)
        NoiseEnvelope.dequantChannel(this, 0)
        bs_add_harmonic[0].fill(0, 64, 0)
        bs_add_harmonic[1].fill(0, 64, 0)
        bs_add_harmonic_flag[0] = ld.readBool()
        if (bs_add_harmonic_flag[0]) sinusoidal_coding(ld, 0)
        bs_extended_data = ld.readBool()
        if (bs_extended_data) {
            var nr_bits_left: Int
            var ps_ext_read = 0
            var cnt = ld.readBits(4)
            if (cnt == 15) {
                cnt += ld.readBits(8)
            }
            nr_bits_left = 8 * cnt
            while (nr_bits_left > 7) {
                var tmp_nr_bits = 0
                bs_extension_id = ld.readBits(2)
                tmp_nr_bits += 2

                /* allow only 1 PS extension element per extension data */if (bs_extension_id == EXTENSION_ID_PS) {
                    if (ps_ext_read == 0) {
                        ps_ext_read = 1
                    } else {
                        /* to be safe make it 3, will switch to "default"
						 * in sbr_extension() */
                        bs_extension_id = 3
                    }
                }
                tmp_nr_bits += sbr_extension(ld, bs_extension_id, nr_bits_left)

                /* check if the data read is bigger than the number of available bits */if (tmp_nr_bits > nr_bits_left) return 1
                nr_bits_left -= tmp_nr_bits
            }

            /* Corrigendum */if (nr_bits_left > 0) {
                ld.readBits(nr_bits_left)
            }
        }
        return 0
    }

    /* table 6 */
    @Throws(AACException::class)
    private fun sbr_channel_pair_element(ld: BitStream): Int {
        var n: Int
        var result: Int
        if (ld.readBool()) {
            //reserved
            ld.readBits(4)
            ld.readBits(4)
        }
        bs_coupling = ld.readBool()
        if (bs_coupling) {
            if (sbr_grid(ld, 0).also { result = it } > 0) return result

            /* need to copy some data from left to right */bs_frame_class[1] = bs_frame_class[0]
            L_E[1] = L_E[0]
            L_Q[1] = L_Q[0]
            bs_pointer[1] = bs_pointer[0]
            n = 0
            while (n <= L_E[0]) {
                t_E[1][n] = t_E[0][n]
                f[1][n] = f[0][n]
                n++
            }
            n = 0
            while (n <= L_Q[0]) {
                t_Q[1][n] = t_Q[0][n]
                n++
            }
            sbr_dtdf(ld, 0)
            sbr_dtdf(ld, 1)
            invf_mode(ld, 0)

            /* more copying */n = 0
            while (n < N_Q) {
                bs_invf_mode[1][n] = bs_invf_mode[0][n]
                n++
            }
            sbr_envelope(ld, 0)
            sbr_noise(ld, 0)
            sbr_envelope(ld, 1)
            sbr_noise(ld, 1)
            bs_add_harmonic[0].fill(0, 64, 0)
            bs_add_harmonic[1].fill(0, 64, 0)
            bs_add_harmonic_flag[0] = ld.readBool()
            if (bs_add_harmonic_flag[0]) sinusoidal_coding(ld, 0)
            bs_add_harmonic_flag[1] = ld.readBool()
            if (bs_add_harmonic_flag[1]) sinusoidal_coding(ld, 1)
        } else {
            val saved_t_E = IntArray(6)
            val saved_t_Q = IntArray(3)
            val saved_L_E = L_E[0]
            val saved_L_Q = L_Q[0]
            val saved_frame_class = bs_frame_class[0]
            n = 0
            while (n < saved_L_E) {
                saved_t_E[n] = t_E[0][n]
                n++
            }
            n = 0
            while (n < saved_L_Q) {
                saved_t_Q[n] = t_Q[0][n]
                n++
            }
            if (sbr_grid(ld, 0).also { result = it } > 0) return result
            if (sbr_grid(ld, 1).also { result = it } > 0) {
                /* restore first channel data as well */
                bs_frame_class[0] = saved_frame_class
                L_E[0] = saved_L_E
                L_Q[0] = saved_L_Q
                n = 0
                while (n < 6) {
                    t_E[0][n] = saved_t_E[n]
                    n++
                }
                n = 0
                while (n < 3) {
                    t_Q[0][n] = saved_t_Q[n]
                    n++
                }
                return result
            }
            sbr_dtdf(ld, 0)
            sbr_dtdf(ld, 1)
            invf_mode(ld, 0)
            invf_mode(ld, 1)
            sbr_envelope(ld, 0)
            sbr_envelope(ld, 1)
            sbr_noise(ld, 0)
            sbr_noise(ld, 1)
            bs_add_harmonic[0].fill(0, 64, 0)
            bs_add_harmonic[1].fill(0, 64, 0)
            bs_add_harmonic_flag[0] = ld.readBool()
            if (bs_add_harmonic_flag[0]) sinusoidal_coding(ld, 0)
            bs_add_harmonic_flag[1] = ld.readBool()
            if (bs_add_harmonic_flag[1]) sinusoidal_coding(ld, 1)
        }
        NoiseEnvelope.dequantChannel(this, 0)
        NoiseEnvelope.dequantChannel(this, 1)
        if (bs_coupling) NoiseEnvelope.unmap(this)
        bs_extended_data = ld.readBool()
        if (bs_extended_data) {
            var nr_bits_left: Int
            var cnt = ld.readBits(4)
            if (cnt == 15) {
                cnt += ld.readBits(8)
            }
            nr_bits_left = 8 * cnt
            while (nr_bits_left > 7) {
                var tmp_nr_bits = 0
                bs_extension_id = ld.readBits(2)
                tmp_nr_bits += 2
                tmp_nr_bits += sbr_extension(ld, bs_extension_id, nr_bits_left)

                /* check if the data read is bigger than the number of available bits */if (tmp_nr_bits > nr_bits_left) return 1
                nr_bits_left -= tmp_nr_bits
            }

            /* Corrigendum */if (nr_bits_left > 0) {
                ld.readBits(nr_bits_left)
            }
        }
        return 0
    }

    /* integer log[2](x): input range [0,10) */
    private fun sbr_log2(`val`: Int): Int {
        val log2tab = intArrayOf(0, 0, 1, 2, 2, 3, 3, 3, 3, 4)
        return if (`val` < 10 && `val` >= 0) log2tab[`val`] else 0
    }

    /* table 7 */
    @Throws(AACException::class)
    private fun sbr_grid(ld: BitStream, ch: Int): Int {
        var i: Int
        var env: Int
        var rel: Int
        var result: Int
        val bs_abs_bord: Int
        val bs_abs_bord_1: Int
        var bs_num_env = 0
        val saved_L_E = L_E[ch]
        val saved_L_Q = L_Q[ch]
        val saved_frame_class = bs_frame_class[ch]
        bs_frame_class[ch] = ld.readBits(2)
        when (bs_frame_class[ch]) {
            FIXFIX -> {
                i = ld.readBits(2)
                bs_num_env = min(1 shl i, 5)
                i = ld.readBit()
                env = 0
                while (env < bs_num_env) {
                    f[ch][env] = i
                    env++
                }
                abs_bord_lead[ch] = 0
                abs_bord_trail[ch] = numTimeSlots
                n_rel_lead[ch] = bs_num_env - 1
                n_rel_trail[ch] = 0
            }

            FIXVAR -> {
                bs_abs_bord = ld.readBits(2) + numTimeSlots
                bs_num_env = ld.readBits(2) + 1
                rel = 0
                while (rel < bs_num_env - 1) {
                    bs_rel_bord[ch][rel] = 2 * ld.readBits(2) + 2
                    rel++
                }
                i = sbr_log2(bs_num_env + 1)
                bs_pointer[ch] = ld.readBits(i)
                env = 0
                while (env < bs_num_env) {
                    f[ch][bs_num_env - env - 1] = ld.readBit()
                    env++
                }
                abs_bord_lead[ch] = 0
                abs_bord_trail[ch] = bs_abs_bord
                n_rel_lead[ch] = 0
                n_rel_trail[ch] = bs_num_env - 1
            }

            VARFIX -> {
                bs_abs_bord = ld.readBits(2)
                bs_num_env = ld.readBits(2) + 1
                rel = 0
                while (rel < bs_num_env - 1) {
                    bs_rel_bord[ch][rel] = 2 * ld.readBits(2) + 2
                    rel++
                }
                i = sbr_log2(bs_num_env + 1)
                bs_pointer[ch] = ld.readBits(i)
                env = 0
                while (env < bs_num_env) {
                    f[ch][env] = ld.readBit()
                    env++
                }
                abs_bord_lead[ch] = bs_abs_bord
                abs_bord_trail[ch] = numTimeSlots
                n_rel_lead[ch] = bs_num_env - 1
                n_rel_trail[ch] = 0
            }

            VARVAR -> {
                bs_abs_bord = ld.readBits(2)
                bs_abs_bord_1 = ld.readBits(2) + numTimeSlots
                bs_num_rel_0[ch] = ld.readBits(2)
                bs_num_rel_1[ch] = ld.readBits(2)
                bs_num_env = min(5, bs_num_rel_0[ch] + bs_num_rel_1[ch] + 1)
                rel = 0
                while (rel < bs_num_rel_0[ch]) {
                    bs_rel_bord_0[ch][rel] = 2 * ld.readBits(2) + 2
                    rel++
                }
                rel = 0
                while (rel < bs_num_rel_1[ch]) {
                    bs_rel_bord_1[ch][rel] = 2 * ld.readBits(2) + 2
                    rel++
                }
                i = sbr_log2(bs_num_rel_0[ch] + bs_num_rel_1[ch] + 2)
                bs_pointer[ch] = ld.readBits(i)
                env = 0
                while (env < bs_num_env) {
                    f[ch][env] = ld.readBit()
                    env++
                }
                abs_bord_lead[ch] = bs_abs_bord
                abs_bord_trail[ch] = bs_abs_bord_1
                n_rel_lead[ch] = bs_num_rel_0[ch]
                n_rel_trail[ch] = bs_num_rel_1[ch]
            }
        }
        if (bs_frame_class[ch] == VARVAR) L_E[ch] = min(bs_num_env, 5) else L_E[ch] =
            min(bs_num_env, 4)
        if (L_E[ch] <= 0) return 1
        if (L_E[ch] > 1) L_Q[ch] = 2 else L_Q[ch] = 1

        /* TODO: this code can probably be integrated into the code above! */if (TFGrid.envelope_time_border_vector(
                this,
                ch
            ).also { result = it } > 0
        ) {
            bs_frame_class[ch] = saved_frame_class
            L_E[ch] = saved_L_E
            L_Q[ch] = saved_L_Q
            return result
        }
        TFGrid.noise_floor_time_border_vector(this, ch)
        return 0
    }

    /* table 8 */
    @Throws(AACException::class)
    private fun sbr_dtdf(ld: BitStream, ch: Int) {
        var i: Int
        i = 0
        while (i < L_E[ch]) {
            bs_df_env[ch][i] = ld.readBit()
            i++
        }
        i = 0
        while (i < L_Q[ch]) {
            bs_df_noise[ch][i] = ld.readBit()
            i++
        }
    }

    /* table 9 */
    @Throws(AACException::class)
    private fun invf_mode(ld: BitStream, ch: Int) {
        var n: Int
        n = 0
        while (n < N_Q) {
            bs_invf_mode[ch][n] = ld.readBits(2)
            n++
        }
    }

    @Throws(AACException::class)
    private fun sbr_extension(ld: BitStream, bs_extension_id: Int, num_bits_left: Int): Int {
        val ret: Int
        return when (bs_extension_id) {
            EXTENSION_ID_PS -> {
                if (ps == null) {
                    ps = PS(sample_rate, numTimeSlotsRate)
                }
                if (psResetFlag) {
                    ps!!.header_read = false
                }
                ret = ps!!.decode(ld)

                /* enable PS if and only if: a header has been decoded */if (!isPSUsed && ps!!.header_read) {
                    isPSUsed = true
                }
                if (ps!!.header_read) {
                    psResetFlag = false
                }
                ret
            }

            else -> {
                bs_extension_data = ld.readBits(6)
                6
            }
        }
    }

    /* table 12 */
    @Throws(AACException::class)
    private fun sinusoidal_coding(ld: BitStream, ch: Int) {
        var n: Int
        n = 0
        while (n < N_high) {
            bs_add_harmonic[ch][n] = ld.readBit()
            n++
        }
    }

    /* table 10 */
    @Throws(AACException::class)
    private fun sbr_envelope(ld: BitStream, ch: Int) {
        var env: Int
        var band: Int
        var delta = 0
        val t_huff: Array<IntArray>
        val f_huff: Array<IntArray>
        if (L_E[ch] == 1 && bs_frame_class[ch] == FIXFIX) amp_res[ch] = false else amp_res[ch] = bs_amp_res
        if (bs_coupling && ch == 1) {
            delta = 1
            if (amp_res[ch]) {
                t_huff = T_HUFFMAN_ENV_BAL_3_0DB
                f_huff = F_HUFFMAN_ENV_BAL_3_0DB
            } else {
                t_huff = T_HUFFMAN_ENV_BAL_1_5DB
                f_huff = F_HUFFMAN_ENV_BAL_1_5DB
            }
        } else {
            delta = 0
            if (amp_res[ch]) {
                t_huff = T_HUFFMAN_ENV_3_0DB
                f_huff = F_HUFFMAN_ENV_3_0DB
            } else {
                t_huff = T_HUFFMAN_ENV_1_5DB
                f_huff = F_HUFFMAN_ENV_1_5DB
            }
        }
        env = 0
        while (env < L_E[ch]) {
            if (bs_df_env[ch][env] == 0) {
                if (bs_coupling && ch == 1) {
                    if (amp_res[ch]) {
                        E[ch][0][env] = ld.readBits(5) shl delta
                    } else {
                        E[ch][0][env] = ld.readBits(6) shl delta
                    }
                } else {
                    if (amp_res[ch]) {
                        E[ch][0][env] = ld.readBits(6) shl delta
                    } else {
                        E[ch][0][env] = ld.readBits(7) shl delta
                    }
                }
                band = 1
                while (band < n[f[ch][env]]) {
                    E[ch][band][env] = decodeHuffman(ld, f_huff) shl delta
                    band++
                }
            } else {
                band = 0
                while (band < n[f[ch][env]]) {
                    E[ch][band][env] = decodeHuffman(ld, t_huff) shl delta
                    band++
                }
            }
            env++
        }
        NoiseEnvelope.extract_envelope_data(this, ch)
    }

    /* table 11 */
    @Throws(AACException::class)
    private fun sbr_noise(ld: BitStream, ch: Int) {
        var noise: Int
        var band: Int
        var delta = 0
        val t_huff: Array<IntArray>
        val f_huff: Array<IntArray>
        if (bs_coupling && ch == 1) {
            delta = 1
            t_huff = T_HUFFMAN_NOISE_BAL_3_0DB
            f_huff = F_HUFFMAN_ENV_BAL_3_0DB
        } else {
            delta = 0
            t_huff = T_HUFFMAN_NOISE_3_0DB
            f_huff = F_HUFFMAN_ENV_3_0DB
        }
        noise = 0
        while (noise < L_Q[ch]) {
            if (bs_df_noise[ch][noise] == 0) {
                if (bs_coupling && ch == 1) {
                    Q[ch][0][noise] = ld.readBits(5) shl delta
                } else {
                    Q[ch][0][noise] = ld.readBits(5) shl delta
                }
                band = 1
                while (band < N_Q) {
                    Q[ch][band][noise] = decodeHuffman(ld, f_huff) shl delta
                    band++
                }
            } else {
                band = 0
                while (band < N_Q) {
                    Q[ch][band][noise] = decodeHuffman(ld, t_huff) shl delta
                    band++
                }
            }
            noise++
        }
        NoiseEnvelope.extract_noise_floor_data(this, ch)
    }

    @Throws(AACException::class)
    private fun decodeHuffman(ld: BitStream, t_huff: Array<IntArray>): Int {
        var bit: Int
        var index = 0
        while (index >= 0) {
            bit = ld.readBit()
            index = t_huff[index][bit]
        }
        return index + 64
    }

    private fun sbr_save_prev_data(ch: Int): Int {
        var i: Int

        /* save data for next frame */kx_prev = kx
        M_prev = M
        bsco_prev = bsco
        L_E_prev[ch] = L_E[ch]

        /* sbr.L_E[ch] can become 0 on files with bit errors */if (L_E[ch] <= 0) return 19
        f_prev[ch] = f[ch][L_E[ch] - 1]
        i = 0
        while (i < MAX_M) {
            E_prev[ch][i] = E[ch][i][L_E[ch] - 1]
            Q_prev[ch][i] = Q[ch][i][L_Q[ch] - 1]
            i++
        }
        i = 0
        while (i < MAX_M) {
            bs_add_harmonic_prev[ch][i] = bs_add_harmonic[ch][i]
            i++
        }
        bs_add_harmonic_flag_prev[ch] = bs_add_harmonic_flag[ch]
        if (l_A[ch] == L_E[ch]) prevEnvIsShort[ch] = 0 else prevEnvIsShort[ch] = -1
        return 0
    }

    private fun sbr_save_matrix(ch: Int) {
        var i: Int
        i = 0
        while (i < tHFGen) {
            for (j in 0..63) {
                Xsbr[ch][i][j][0] = Xsbr[ch][i + numTimeSlotsRate][j][0]
                Xsbr[ch][i][j][1] = Xsbr[ch][i + numTimeSlotsRate][j][1]
            }
            i++
        }
        i = tHFGen
        while (i < MAX_NTSRHFG) {
            for (j in 0..63) {
                Xsbr[ch][i][j][0] = 0f
                Xsbr[ch][i][j][1] = 0f
            }
            i++
        }
    }

    private fun sbr_process_channel(
        channel_buf: FloatArray, X: Array<Array<FloatArray>>,
        ch: Int, dont_process: Boolean
    ): Int {
        var dont_process = dont_process
        var k: Int
        var l: Int
        var ret = 0
        bsco = 0

        /* subband analysis */if (dont_process) qmfa[ch]!!
            .sbr_qmf_analysis_32(this, channel_buf, Xsbr[ch], tHFGen, 32) else qmfa[ch]!!
            .sbr_qmf_analysis_32(this, channel_buf, Xsbr[ch], tHFGen, kx)
        if (!dont_process) {
            /* insert high frequencies here */
            /* hf generation using patching */
            HFGeneration.hf_generation(this, Xsbr[ch], Xsbr[ch], ch)


            /* hf adjustment */ret =
                HFAdjustment.hf_adjustment(this, Xsbr[ch], ch)
            if (ret > 0) {
                dont_process = true
            }
        }
        if (just_seeked || dont_process) {
            l = 0
            while (l < numTimeSlotsRate) {
                k = 0
                while (k < 32) {
                    X[l][k][0] = Xsbr[ch][l + tHFAdj][k][0]
                    X[l][k][1] = Xsbr[ch][l + tHFAdj][k][1]
                    k++
                }
                k = 32
                while (k < 64) {
                    X[l][k][0] = 0f
                    X[l][k][1] = 0f
                    k++
                }
                l++
            }
        } else {
            l = 0
            while (l < numTimeSlotsRate) {
                var kx_band: Int
                var M_band: Int
                var bsco_band: Int
                if (l < t_E[ch][0]) {
                    kx_band = kx_prev
                    M_band = M_prev
                    bsco_band = bsco_prev
                } else {
                    kx_band = kx
                    M_band = M
                    bsco_band = bsco
                }
                k = 0
                while (k < kx_band + bsco_band) {
                    X[l][k][0] = Xsbr[ch][l + tHFAdj][k][0]
                    X[l][k][1] = Xsbr[ch][l + tHFAdj][k][1]
                    k++
                }
                k = kx_band + bsco_band
                while (k < kx_band + M_band) {
                    X[l][k][0] = Xsbr[ch][l + tHFAdj][k][0]
                    X[l][k][1] = Xsbr[ch][l + tHFAdj][k][1]
                    k++
                }
                k = max(kx_band + bsco_band, kx_band + M_band)
                while (k < 64) {
                    X[l][k][0] = 0f
                    X[l][k][1] = 0f
                    k++
                }
                l++
            }
        }
        return ret
    }

    fun process(
        left_chan: FloatArray, right_chan: FloatArray,
        just_seeked: Boolean
    ): Int {
        var dont_process = false
        var ret = 0
        val X = Array(MAX_NTSR) { Array(64) { FloatArray(2) } }

        /* case can occur due to bit errors */if (!stereo) return 21
        if (this.ret != 0 || header_count == 0) {
            /* don't process just upsample */
            dont_process = true

            /* Re-activate reset for next frame */if (this.ret != 0 && Reset) bs_start_freq_prev = -1
        }
        this.just_seeked = just_seeked
        this.ret += sbr_process_channel(left_chan, X, 0, dont_process)
        /* subband synthesis */if (downSampledSBR) {
            qmfs[0]!!.sbr_qmf_synthesis_32(this, X, left_chan)
        } else {
            qmfs[0]!!.sbr_qmf_synthesis_64(this, X, left_chan)
        }
        this.ret += sbr_process_channel(right_chan, X, 1, dont_process)
        /* subband synthesis */if (downSampledSBR) {
            qmfs[1]!!.sbr_qmf_synthesis_32(this, X, right_chan)
        } else {
            qmfs[1]!!.sbr_qmf_synthesis_64(this, X, right_chan)
        }
        if (bs_header_flag) this.just_seeked = false
        if (header_count != 0 && this.ret == 0) {
            ret = sbr_save_prev_data(0)
            if (ret != 0) return ret
            ret = sbr_save_prev_data(1)
            if (ret != 0) return ret
        }
        sbr_save_matrix(0)
        sbr_save_matrix(1)
        frame++
        return 0
    }

    fun process(
        channel: FloatArray,
        just_seeked: Boolean
    ): Int {
        var dont_process = false
        var ret = 0
        val X = Array(MAX_NTSR) { Array(64) { FloatArray(2) } }

        /* case can occur due to bit errors */if (stereo) return 21
        if (this.ret != 0 || header_count == 0) {
            /* don't process just upsample */
            dont_process = true

            /* Re-activate reset for next frame */if (this.ret != 0 && Reset) bs_start_freq_prev = -1
        }
        this.just_seeked = just_seeked
        this.ret += sbr_process_channel(channel, X, 0, dont_process)
        /* subband synthesis */if (downSampledSBR) {
            qmfs[0]!!.sbr_qmf_synthesis_32(this, X, channel)
        } else {
            qmfs[0]!!.sbr_qmf_synthesis_64(this, X, channel)
        }
        if (bs_header_flag) this.just_seeked = false
        if (header_count != 0 && this.ret == 0) {
            ret = sbr_save_prev_data(0)
            if (ret != 0) return ret
        }
        sbr_save_matrix(0)
        frame++
        return 0
    }

    fun processPS(
        left_channel: FloatArray, right_channel: FloatArray,
        just_seeked: Boolean
    ): Int {
        var l: Int
        var k: Int
        var dont_process = false
        var ret = 0
        val X_left = Array(38) { Array(64) { FloatArray(2) } }
        val X_right = Array(38) { Array(64) { FloatArray(2) } }

        /* case can occur due to bit errors */if (stereo) return 21
        if (this.ret != 0 || header_count == 0) {
            /* don't process just upsample */
            dont_process = true

            /* Re-activate reset for next frame */if (this.ret != 0 && Reset) bs_start_freq_prev = -1
        }
        this.just_seeked = just_seeked
        if (qmfs[1] == null) {
            qmfs[1] = SynthesisFilterbank(if (downSampledSBR) 32 else 64)
        }
        this.ret += sbr_process_channel(left_channel, X_left, 0, dont_process)

        /* copy some extra data for PS */l = numTimeSlotsRate
        while (l < numTimeSlotsRate + 6) {
            k = 0
            while (k < 5) {
                X_left[l][k][0] = Xsbr[0][tHFAdj + l][k][0]
                X_left[l][k][1] = Xsbr[0][tHFAdj + l][k][1]
                k++
            }
            l++
        }

        /* perform parametric stereo */ps!!.process(X_left, X_right)

        /* subband synthesis */if (downSampledSBR) {
            qmfs[0]!!.sbr_qmf_synthesis_32(this, X_left, left_channel)
            qmfs[1]!!.sbr_qmf_synthesis_32(this, X_right, right_channel)
        } else {
            qmfs[0]!!.sbr_qmf_synthesis_64(this, X_left, left_channel)
            qmfs[1]!!.sbr_qmf_synthesis_64(this, X_right, right_channel)
        }
        if (bs_header_flag) this.just_seeked = false
        if (header_count != 0 && this.ret == 0) {
            ret = sbr_save_prev_data(0)
            if (ret != 0) return ret
        }
        sbr_save_matrix(0)
        frame++
        return 0
    }
}
