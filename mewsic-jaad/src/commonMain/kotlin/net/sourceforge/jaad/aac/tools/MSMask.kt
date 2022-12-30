package net.sourceforge.jaad.aac.tools
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.aac.AACException

/**
 * The MSMask indicates, if MS is applied to a specific ICStream.
 * @author in-somnia
 */
enum class MSMask(private val num: Int) {
    TYPE_ALL_0(0), TYPE_USED(1), TYPE_ALL_1(2), TYPE_RESERVED(3);

    companion object {
        @Throws(AACException::class)
        fun forInt(i: Int): MSMask {
            val m: MSMask
            m = when (i) {
                0 -> TYPE_ALL_0
                1 -> TYPE_USED
                2 -> TYPE_ALL_1
                3 -> TYPE_RESERVED
                else -> throw AACException("unknown MS mask type")
            }
            return m
        }
    }
}
