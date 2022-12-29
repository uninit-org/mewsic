package net.sourceforge.jaad.mp4.boxes.impl

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
    var sampleNumbers: LongArray
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4) as Int
        sampleNumbers = LongArray(entryCount)
        for (i in 0 until entryCount) {
            sampleNumbers[i] = `in`.readBytes(4)
        }
    }
}
