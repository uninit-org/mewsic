package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.boxes.FullBox

class SampleSizeBox : FullBox("Sample Size Box") {
    private var sampleCount: Long = 0
    lateinit var sampleSizes: LongArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val compact = type == BoxTypes.COMPACT_SAMPLE_SIZE_BOX
        val sampleSize: Int
        sampleSize = if (compact) {
            `in`.skipBytes(3)
            `in`.read()
        } else `in`.readBytes(4).toInt()
        sampleCount = `in`.readBytes(4)
        sampleSizes = LongArray(sampleCount.toInt())
        if (compact) {
            //compact: sampleSize can be 4, 8 or 16 bits
            if (sampleSize == 4) {
                var x: Int
                var i = 0
                while (i < sampleCount) {
                    x = `in`.read()
                    sampleSizes[i] = (x shr 4 and 0xF).toLong()
                    sampleSizes[i + 1] = (x and 0xF).toLong()
                    i += 2
                }
            } else readSizes(`in`, sampleSize / 8)
        } else if (sampleSize == 0) readSizes(`in`, 4) else Arrays.fill(sampleSizes, sampleSize.toLong())
    }

    @Throws(Exception::class)
    private fun readSizes(`in`: MP4InputStream, len: Int) {
        for (i in 0 until sampleCount) {
            sampleSizes[i.toInt()] = `in`.readBytes(len)
        }
    }

    fun getSampleCount(): Int {
        return sampleCount.toInt()
    }
}
