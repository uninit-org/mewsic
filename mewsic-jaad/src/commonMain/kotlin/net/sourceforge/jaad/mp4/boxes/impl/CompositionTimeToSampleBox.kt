package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.boxes.FullBox

/**
 * This box provides the offset between decoding time and composition time.
 * Since decoding time must be less than the composition time, the offsets are
 * expressed as unsigned numbers such that
 * CT(n) = DT(n) + CTTS(n)
 * where CTTS(n) is the (uncompressed) table entry for sample n.
 *
 * The composition time to sample table is optional and must only be present if
 * DT and CT differ for any samples.
 *
 * Hint tracks do not use this box.
 *
 * @author in-somnia
 */
class CompositionTimeToSampleBox : FullBox("Time To Sample Box") {
    var sampleCounts: LongArray
        private set
    var sampleOffsets: LongArray
        private set

    @Throws(Exception::class)
    override override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4) as Int
        sampleCounts = LongArray(entryCount)
        sampleOffsets = LongArray(entryCount)
        for (i in 0 until entryCount) {
            sampleCounts[i] = `in`.readBytes(4)
            sampleOffsets[i] = `in`.readBytes(4)
        }
    }
}
