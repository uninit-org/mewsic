package net.sourceforge.jaad.aac.sbr

import net.sourceforge.jaad.aac.SampleFrequency
import net.sourceforge.jaad.aac.sbr.Constants.Companion.HI_RES
import net.sourceforge.jaad.aac.sbr.Constants.Companion.LO_RES
import net.sourceforge.jaad.aac.sbr.Constants.Companion.OFFSET
import net.sourceforge.jaad.aac.sbr.Constants.Companion.offsetIndexTable
import net.sourceforge.jaad.aac.sbr.Constants.Companion.startMinTable
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

internal object FBT : Constants {
    /* calculate the start QMF channel for the master frequency band table */ /* parameter is also called k0 */
    fun qmf_start_channel(
        bs_start_freq: Int, bs_samplerate_mode: Int,
        sample_rate: SampleFrequency
    ): Int {
        val startMin: Int = startMinTable.get(sample_rate.index)
        val offsetIndex: Int = offsetIndexTable.get(sample_rate.index)
        return if (bs_samplerate_mode != 0) {
            startMin + OFFSET.get(offsetIndex).get(bs_start_freq)
        } else {
            startMin + OFFSET.get(6).get(bs_start_freq)
        }
    }

    private val stopMinTable = intArrayOf(
        13, 15, 20, 21, 23,
        32, 32, 35, 48, 64, 70, 96
    )
    private val STOP_OFFSET_TABLE = arrayOf(
        intArrayOf(0, 2, 4, 6, 8, 11, 14, 18, 22, 26, 31, 37, 44, 51),
        intArrayOf(0, 2, 4, 6, 8, 11, 14, 18, 22, 26, 31, 36, 42, 49),
        intArrayOf(0, 2, 4, 6, 8, 11, 14, 17, 21, 25, 29, 34, 39, 44),
        intArrayOf(0, 2, 4, 6, 8, 11, 14, 17, 20, 24, 28, 33, 38, 43),
        intArrayOf(0, 2, 4, 6, 8, 11, 14, 17, 20, 24, 28, 32, 36, 41),
        intArrayOf(0, 2, 4, 6, 8, 10, 12, 14, 17, 20, 23, 26, 29, 32),
        intArrayOf(0, 2, 4, 6, 8, 10, 12, 14, 17, 20, 23, 26, 29, 32),
        intArrayOf(0, 1, 3, 5, 7, 9, 11, 13, 15, 17, 20, 23, 26, 29),
        intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, -1, -2, -3, -4, -5, -6, -6, -6, -6, -6, -6, -6, -6),
        intArrayOf(0, -3, -6, -9, -12, -15, -18, -20, -22, -24, -26, -28, -30, -32)
    )

    /* calculate the stop QMF channel for the master frequency band table */ /* parameter is also called k2 */
    fun qmf_stop_channel(
        bs_stop_freq: Int, sample_rate: SampleFrequency,
        k0: Int
    ): Int {
        return when (bs_stop_freq) {
            15 -> {
                min(64, k0 * 3)
            }

            14 -> {
                min(64, k0 * 2)
            }

            else -> {
                min(64, STOP_OFFSET_TABLE[sample_rate.index][min(bs_stop_freq, 13)])
            }
        }
    }

    /* calculate the master frequency table from k0, k2, bs_freq_scale
	 and bs_alter_scale

	 version for bs_freq_scale = 0
	 */
    fun master_frequency_table_fs0(
        sbr: SBR, k0: Int, k2: Int,
        bs_alter_scale: Boolean
    ): Int {
        val incr: Int
        var k: Int
        val dk: Int
        var nrBands: Int
        val k2Achieved: Int
        var k2Diff: Int
        val vDk = IntArray(64)

        /* mft only defined for k2 > k0 */if (k2 <= k0) {
            sbr.N_master = 0
            return 1
        }
        dk = if (bs_alter_scale) 2 else 1
        nrBands = if (bs_alter_scale) {
            k2 - k0 + 2 shr 2 shl 1
        } else {
            k2 - k0 shr 1 shl 1
        }
        nrBands = min(nrBands, 63)
        if (nrBands <= 0) return 1
        k2Achieved = k0 + nrBands * dk
        k2Diff = k2 - k2Achieved
        k = 0
        while (k < nrBands) {
            vDk[k] = dk
            k++
        }
        if (k2Diff != 0) {
            incr = if (k2Diff > 0) -1 else 1
            k = if (k2Diff > 0) nrBands - 1 else 0
            while (k2Diff != 0) {
                vDk[k] -= incr
                k += incr
                k2Diff += incr
            }
        }
        sbr.f_master[0] = k0
        k = 1
        while (k <= nrBands) {
            sbr.f_master[k] = sbr.f_master.get(k - 1) + vDk[k - 1]
            k++
        }
        sbr.N_master = nrBands
        sbr.N_master = min(sbr.N_master, 64)
        return 0
    }

    /*
	 This function finds the number of bands using this formula:
	 bands * log(a1/a0)/log(2.0) + 0.5
	 */
    /*
        float div = (float) Math.log(2.0);
		if(warp!=0) div *= 1.3f;

		return (int) (bands*Math.log((float) a1/(float) a0)/div+0.5);
     */
    fun find_bands(warp: Int, bands: Int, a0: Int, a1: Int): Int {
        var div = log2(2.0)
        if (warp != 0) div *= 1.3
        return (bands * log2(a1.toFloat() / a0.toFloat()) / div + 0.5).toInt()
    }

    fun find_initial_power(bands: Int, a0: Int, a1: Int): Float {
        return ((a1.toFloat() / a0.toFloat()).toDouble().pow((1.0f / bands.toFloat()).toDouble()))
            .toFloat()
    }

    /*
	 version for bs_freq_scale > 0
	 */
    fun master_frequency_table(
        sbr: SBR, k0: Int, k2: Int,
        bs_freq_scale: Int, bs_alter_scale: Boolean
    ): Int {
        var k: Int
        val bands: Int
        val twoRegions: Boolean
        val k1: Int
        var nrBand0: Int
        var nrBand1: Int
        val vDk0 = IntArray(64)
        val vDk1 = IntArray(64)
        val vk0 = IntArray(64)
        val vk1 = IntArray(64)
        val temp1 = intArrayOf(6, 5, 4)
        var q: Float
        var qk: Float
        var A_1: Int

        /* mft only defined for k2 > k0 */if (k2 <= k0) {
            sbr.N_master = 0
            return 1
        }
        bands = temp1[bs_freq_scale - 1]
        if (k2.toFloat() / k0.toFloat() > 2.2449) {
            twoRegions = true
            k1 = k0 shl 1
        } else {
            twoRegions = false
            k1 = k2
        }
        nrBand0 = 2 * find_bands(0, bands, k0, k1)
        nrBand0 = min(nrBand0, 63)
        if (nrBand0 <= 0) return 1
        q = find_initial_power(nrBand0, k0, k1)
        qk = k0.toFloat()
        A_1 = (qk + 0.5f).toInt()
        k = 0
        while (k <= nrBand0) {
            val A_0 = A_1
            qk *= q
            A_1 = (qk + 0.5f).toInt()
            vDk0[k] = A_1 - A_0
            k++
        }

        /* needed? */
        //qsort(vDk0, nrBand0, sizeof(vDk0[0]), longcmp);
        // FIXME we should only sort the first nrBand0 elements
        vDk0.sort()
        vk0[0] = k0
        k = 1
        while (k <= nrBand0) {
            vk0[k] = vk0[k - 1] + vDk0[k - 1]
            if (vDk0[k - 1] == 0) return 1
            k++
        }
        if (!twoRegions) {
            k = 0
            while (k <= nrBand0) {
                sbr.f_master[k] = vk0[k]
                k++
            }
            sbr.N_master = nrBand0
            sbr.N_master = min(sbr.N_master, 64)
            return 0
        }
        /* warped */nrBand1 = 2 * find_bands(1 /* warped */, bands, k1, k2)
        nrBand1 = min(nrBand1, 63)
        q = find_initial_power(nrBand1, k1, k2)
        qk = k1.toFloat()
        A_1 = (qk + 0.5f).toInt()
        k = 0
        while (k <= nrBand1 - 1) {
            val A_0 = A_1
            qk *= q
            A_1 = (qk + 0.5f).toInt()
            vDk1[k] = A_1 - A_0
            k++
        }
        if (vDk1[0] < vDk0[nrBand0 - 1]) {
            val change: Int

            /* needed? */
            //qsort(vDk1, nrBand1+1, sizeof(vDk1[0]), longcmp);
            // FIXME: should be equiv to java.util.Arrays.sort(vDk1, 0, nrBand1 + 1)
            vDk1.sort()
            change = vDk0[nrBand0 - 1] - vDk1[0]
            vDk1[0] = vDk0[nrBand0 - 1]
            vDk1[nrBand1 - 1] = vDk1[nrBand1 - 1] - change
        }

        /* needed? */
        //qsort(vDk1, nrBand1, sizeof(vDk1[0]), longcmp);
        // FIXME: should be equiv to java.util.Arrays.sort(vDk1, 0, nrBand1)
        vDk1.sort()
        vk1[0] = k1
        k = 1
        while (k <= nrBand1) {
            vk1[k] = vk1[k - 1] + vDk1[k - 1]
            if (vDk1[k - 1] == 0) return 1
            k++
        }
        sbr.N_master = nrBand0 + nrBand1
        sbr.N_master = min(sbr.N_master, 64)
        k = 0
        while (k <= nrBand0) {
            sbr.f_master[k] = vk0[k]
            k++
        }
        k = nrBand0 + 1
        while (k <= sbr.N_master) {
            sbr.f_master[k] = vk1[k - nrBand0]
            k++
        }
        return 0
    }

    /* calculate the derived frequency border tables from f_master */
    fun derived_frequency_table(
        sbr: SBR, bs_xover_band: Int,
        k2: Int
    ): Int {
        var k: Int
        var i = 0
        val minus: Int

        /* The following relation shall be satisfied: bs_xover_band < N_Master */if (sbr.N_master <= bs_xover_band) return 1
        sbr.N_high = sbr.N_master - bs_xover_band
        sbr.N_low = (sbr.N_high shr 1) + (sbr.N_high - (sbr.N_high shr 1 shl 1))
        sbr.n[0] = sbr.N_low
        sbr.n[1] = sbr.N_high
        k = 0
        while (k <= sbr.N_high) {
            sbr.f_table_res[HI_RES][k] = sbr.f_master.get(k + bs_xover_band)
            k++
        }
        sbr.M = sbr.f_table_res[HI_RES][sbr.N_high] - sbr.f_table_res[HI_RES][0]
        sbr.kx = sbr.f_table_res[HI_RES][0]
        if (sbr.kx > 32) return 1
        if (sbr.kx + sbr.M > 64) return 1
        minus = if (sbr.N_high and 1 != 0) 1 else 0
        k = 0
        while (k <= sbr.N_low) {
            i = if (k == 0) 0 else 2 * k - minus
            sbr.f_table_res.get(LO_RES)[k] = sbr.f_table_res[HI_RES][i]
            k++
        }
        sbr.N_Q = 0
        if (sbr.bs_noise_bands == 0) {
            sbr.N_Q = 1
        } else {
            sbr.N_Q = max(1, find_bands(0, sbr.bs_noise_bands, sbr.kx, k2))
            sbr.N_Q = min(5, sbr.N_Q)
        }
        k = 0
        while (k <= sbr.N_Q) {
            if (k == 0) {
                i = 0
            } else {
                /* i = i + (int32_t)((sbr.N_low - i)/(sbr.N_Q + 1 - k)); */
                i += (sbr.N_low - i) / (sbr.N_Q + 1 - k)
            }
            sbr.f_table_noise[k] = sbr.f_table_res[LO_RES][i]
            k++
        }

        /* build table for mapping k to g in hf patching */k = 0
        while (k < 64) {
            var g: Int
            g = 0
            while (g < sbr.N_Q) {
                if (sbr.f_table_noise[g] <= k && k < sbr.f_table_noise[g + 1]) {
                    sbr.table_map_k_to_g[k] = g
                    break
                }
                g++
            }
            k++
        }
        return 0
    }

    /* TODO: blegh, ugly */ /* Modified to calculate for all possible bs_limiter_bands always
	 * This reduces the number calls to this functions needed (now only on
	 * header reset)
	 */
    private val limiterBandsCompare = floatArrayOf(
        1.327152f,
        1.185093f, 1.119872f
    )

    fun limiter_frequency_table(sbr: SBR) {
        var k: Int
        var s: Int
        var nrLim: Int
        sbr.f_table_lim[0][0] = sbr.f_table_res[LO_RES][0] - sbr.kx
        sbr.f_table_lim[0][1] = sbr.f_table_res[LO_RES][sbr.N_low] - sbr.kx
        sbr.N_L[0] = 1
        s = 1
        while (s < 4) {
            val limTable = IntArray(100 /*TODO*/)
            val patchBorders = IntArray(64 /*??*/)
            patchBorders[0] = sbr.kx
            k = 1
            while (k <= sbr.noPatches) {
                patchBorders[k] = patchBorders[k - 1] + sbr.patchNoSubbands.get(k - 1)
                k++
            }
            k = 0
            while (k <= sbr.N_low) {
                limTable[k] = sbr.f_table_res.get(LO_RES).get(k)
                k++
            }
            k = 1
            while (k < sbr.noPatches) {
                limTable[k + sbr.N_low] = patchBorders[k]
                k++
            }

            /* needed */
            //qsort(limTable, sbr.noPatches+sbr.N_low, sizeof(limTable[0]), longcmp);
            // FIXME: should be equiv to java.util.Arrays.sort(limTable, 0, sbr.noPatches + sbr.N_low)
            limTable.sort()
            k = 1
            nrLim = sbr.noPatches + sbr.N_low - 1
            if (nrLim < 0) // TODO: BIG FAT PROBLEM
                return
            restart@ while (k <= nrLim) {
                var nOctaves: Float
                nOctaves = if (limTable[k - 1] != 0) limTable[k].toFloat() / limTable[k - 1].toFloat() else 0f
                if (nOctaves < limiterBandsCompare[s - 1]) {
                    var i: Int
                    if (limTable[k] != limTable[k - 1]) {
                        var found = false
                        var found2 = false
                        i = 0
                        while (i <= sbr.noPatches) {
                            if (limTable[k] == patchBorders[i]) found = true
                            i++
                        }
                        if (found) {
                            found2 = false
                            i = 0
                            while (i <= sbr.noPatches) {
                                if (limTable[k - 1] == patchBorders[i]) found2 = true
                                i++
                            }
                            if (found2) {
                                k++
                                continue
                            } else {
                                /* remove (k-1)th element */
                                limTable[k - 1] = sbr.f_table_res.get(LO_RES).get(sbr.N_low)
                                //qsort(limTable, sbr.noPatches+sbr.N_low, sizeof(limTable[0]), longcmp);
                                // FIXME: should be equiv to java.util.Arrays.sort(limTable, 0, sbr.noPatches + sbr.N_low)
                                limTable.sort()
                                nrLim--
                                continue
                            }
                        }
                    }
                    /* remove kth element */limTable[k] = sbr.f_table_res.get(LO_RES).get(sbr.N_low)
                    //qsort(limTable, nrLim, sizeof(limTable[0]), longcmp);
                    // FIXME: should be equv to java.util.Arrays.sort(limTable, 0, nrLim)
                    limTable.sort()
                    nrLim--
                    //continue;
                } else {
                    k++
                    //continue;
                }
            }
            sbr.N_L[s] = nrLim
            k = 0
            while (k <= nrLim) {
                sbr.f_table_lim[s][k] = limTable[k] - sbr.kx
                k++
            }
            s++
        }
    }
}
