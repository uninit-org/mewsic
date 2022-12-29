package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.boxes.FullBox

class SampleSizeBox : FullBox("Sample Size Box") {
    private var sampleCount: Long = 0
    var sampleSizes: LongArray
        private set

    @Throws(java.io.IOException::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val compact = type === BoxTypes.COMPACT_SAMPLE_SIZE_BOX
        val sampleSize: Int
        sampleSize = if (compact) {
            `in`.skipBytes(3)
            `in`.read()
        } else `in`.readBytes(4)
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
        } else if (sampleSize == 0) readSizes(`in`, 4) else java.util.Arrays.fill(sampleSizes, sampleSize.toLong())
    }

    @Throws(java.io.IOException::class)
    private fun readSizes(`in`: MP4InputStream, len: Int) {
        for (i in 0 until sampleCount) {
            sampleSizes[i.toInt()] = `in`.readBytes(len)
        }
    }

    fun getSampleCount(): Int {
        return sampleCount.toInt()
    }
}
