package net.sourceforge.jaad.aac.syntax

interface Constants {
    companion object {
        const val MAX_ELEMENTS = 16
        const val BYTE_MASK = 0xFF
        const val MIN_INPUT_SIZE = 768 //6144 bits/channel

        //frame length
        const val WINDOW_LEN_LONG = 1024
        const val WINDOW_LEN_SHORT = WINDOW_LEN_LONG / 8
        const val WINDOW_SMALL_LEN_LONG = 960
        const val WINDOW_SMALL_LEN_SHORT = WINDOW_SMALL_LEN_LONG / 8

        //element types
        const val ELEMENT_SCE = 0
        const val ELEMENT_CPE = 1
        const val ELEMENT_CCE = 2
        const val ELEMENT_LFE = 3
        const val ELEMENT_DSE = 4
        const val ELEMENT_PCE = 5
        const val ELEMENT_FIL = 6
        const val ELEMENT_END = 7

        //maximum numbers
        const val MAX_WINDOW_COUNT = 8
        const val MAX_WINDOW_GROUP_COUNT = MAX_WINDOW_COUNT
        const val MAX_LTP_SFB = 40
        const val MAX_SECTIONS = 120
        const val MAX_MS_MASK = 128
        const val SQRT2 = 1.4142135f
    }
}
