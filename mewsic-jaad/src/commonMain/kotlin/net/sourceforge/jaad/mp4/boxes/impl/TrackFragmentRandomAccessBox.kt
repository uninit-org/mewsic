package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class TrackFragmentRandomAccessBox : FullBox("Track Fragment Random Access Box") {
    /**
     * The track ID is an integer identifying the associated track.
     *
     * @return the track ID
     */
    var trackID: Long = 0
        private set
    var entryCount = 0
        private set

    /**
     * The time is an integer that indicates the presentation time of the random
     * access sample in units defined in the 'mdhd' of the associated track.
     *
     * @return the times of all entries
     */
    val times: LongArray

    /**
     * The moof-Offset is an integer that gives the offset of the 'moof' used in
     * the an entry. Offset is the byte-offset between the beginning of the file
     * and the beginning of the 'moof'.
     *
     * @return the offsets for all entries
     */
    val moofOffsets: LongArray

    /**
     * The 'traf' number that contains the random accessible sample. The number
     * ranges from 1 (the first 'traf' is numbered 1) in each 'moof'.
     *
     * @return the 'traf' numbers for all entries
     */
    val trafNumbers: LongArray

    /**
     * The 'trun' number that contains the random accessible sample. The number
     * ranges from 1 in each 'traf'.
     *
     * @return the 'trun' numbers for all entries
     */
    val trunNumbers: LongArray

    /**
     * The sample number that contains the random accessible sample. The number
     * ranges from 1 in each 'trun'.
     *
     * @return the sample numbers for all entries
     */
    val sampleNumbers: LongArray
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        trackID = `in`.readBytes(4)
        //26 bits reserved, 2 bits trafSizeLen, 2 bits trunSizeLen, 2 bits sampleSizeLen
        val l: Long = `in`.readBytes(4)
        val trafNumberLen = (l shr 4 and 0x3L).toInt() + 1
        val trunNumberLen = (l shr 2 and 0x3L).toInt() + 1
        val sampleNumberLen = (l and 0x3L).toInt() + 1
        entryCount = `in`.readBytes(4) as Int
        val len = if (version === 1) 8 else 4
        for (i in 0 until entryCount) {
            times[i] = `in`.readBytes(len)
            moofOffsets[i] = `in`.readBytes(len)
            trafNumbers[i] = `in`.readBytes(trafNumberLen)
            trunNumbers[i] = `in`.readBytes(trunNumberLen)
            sampleNumbers[i] = `in`.readBytes(sampleNumberLen)
        }
    }
}
