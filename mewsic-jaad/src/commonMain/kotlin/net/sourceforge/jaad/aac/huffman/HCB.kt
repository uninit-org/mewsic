package net.sourceforge.jaad.aac.huffman
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
interface HCB {
    companion object {
        const val ZERO_HCB = 0
        const val ESCAPE_HCB = 11
        const val NOISE_HCB = 13
        const val INTENSITY_HCB2 = 14
        const val INTENSITY_HCB = 15

        //
        const val FIRST_PAIR_HCB = 5
    }
}
