package net.sourceforge.jaad.mp4.boxes.impl
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * This box provides a compact marking of the random access points within the
 * stream. The table is arranged in strictly increasing order of sample number.
 *
 * If the sync sample box is not present, every sample is a random access point.
 *
 * @author in-somnia
 */
class SyncSampleBox : FullBox("Sync Sample Box") {
    /**
     * Gives the numbers of the samples for each entry that are random access
     * points in the stream.
     *
     * @return a list of sample numbers
     */
    lateinit var sampleNumbers: LongArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4).toInt()
        sampleNumbers = LongArray(entryCount)
        for (i in 0 until entryCount) {
            sampleNumbers[i] = `in`.readBytes(4)
        }
    }
}
