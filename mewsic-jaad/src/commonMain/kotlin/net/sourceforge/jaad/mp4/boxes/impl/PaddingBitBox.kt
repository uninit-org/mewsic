package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * In some streams the media samples do not occupy all bits of the bytes given
 * by the sample size, and are padded at the end to a byte boundary. In some
 * cases, it is necessary to record externally the number of padding bits used.
 * This table supplies that information.
 *
 * @author in-somnia
 */
class PaddingBitBox : FullBox("Padding Bit Box") {
    /**
     * Integer values from 0 to 7, indicating the number of bits at the end of
     * sample (i*2)+1.
     */
    var pad1: IntArray
        private set

    /**
     * Integer values from 0 to 7, indicating the number of bits at the end of
     * sample (i*2)+2.
     */
    var pad2: IntArray
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val sampleCount = (`in`.readBytes(4) + 1) as Int / 2
        pad1 = IntArray(sampleCount)
        pad2 = IntArray(sampleCount)
        var b: Byte
        for (i in 0 until sampleCount) {
            b = `in`.read() as Byte
            //1 bit reserved
            //3 bits pad1
            pad1[i] = b.toInt() shr 4 and 7
            //1 bit reserved
            //3 bits pad2
            pad2[i] = b.toInt() and 7
        }
    }
}
