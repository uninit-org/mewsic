package net.sourceforge.jaad.aac.sbr
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
internal interface Constants {
    companion object {
        val startMinTable = intArrayOf(
            7, 7, 10, 11, 12, 16, 16,
            17, 24, 32, 35, 48
        )
        val offsetIndexTable = intArrayOf(
            5, 5, 4, 4, 4, 3, 2, 1, 0,
            6, 6, 6
        )
        val OFFSET = arrayOf(
            intArrayOf(-8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7),
            intArrayOf(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13),
            intArrayOf(-5, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16),
            intArrayOf(-6, -4, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16),
            intArrayOf(-4, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16, 20),
            intArrayOf(-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16, 20, 24),
            intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16, 20, 24, 28, 33)
        )
        const val EXTENSION_ID_PS = 2
        const val MAX_NTSRHFG = 40 //maximum of number_time_slots * rate + HFGen. 16*2+8
        const val MAX_NTSR = 32 //max number_time_slots * rate, ok for DRM and not DRM mode
        const val MAX_M = 49 //maximum value for M
        const val MAX_L_E = 5 //maximum value for L_E
        const val EXT_SBR_DATA = 13
        const val EXT_SBR_DATA_CRC = 14
        const val FIXFIX = 0
        const val FIXVAR = 1
        const val VARFIX = 2
        const val VARVAR = 3
        const val LO_RES = 0
        const val HI_RES = 1
        const val NO_TIME_SLOTS_960 = 15
        const val NO_TIME_SLOTS = 16
        const val RATE = 2
        const val NOISE_FLOOR_OFFSET = 6
        const val T_HFGEN = 8
        const val T_HFADJ = 2
    }
}
