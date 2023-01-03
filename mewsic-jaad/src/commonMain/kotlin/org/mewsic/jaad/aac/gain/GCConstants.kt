package net.sourceforge.jaad.aac.gain

internal interface GCConstants {
    companion object {
        const val BANDS = 4
        const val MAX_CHANNELS = 5
        const val NPQFTAPS = 96
        const val NPEPARTS = 64 //number of pre-echo inhibition parts
        const val ID_GAIN = 16
        val LN_GAIN = intArrayOf(
            -4, -3, -2, -1, 0, 1, 2, 3,
            4, 5, 6, 7, 8, 9, 10, 11
        )
    }
}
