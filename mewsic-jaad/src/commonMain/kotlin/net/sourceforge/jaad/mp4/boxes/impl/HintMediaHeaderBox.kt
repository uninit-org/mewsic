package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The hint media header contains general information, independent of the
 * protocol, for hint tracks.
 *
 * @author in-somnia
 */
class HintMediaHeaderBox : FullBox("Hint Media Header Box") {
    /**
     * The maximum PDU size gives the size in bytes of the largest PDU (protocol
     * data unit) in this hint stream.
     */
    var maxPDUsize: Long = 0
        private set

    /**
     * The average PDU size gives the average size of a PDU over the entire
     * presentation.
     */
    var averagePDUsize: Long = 0
        private set

    /**
     * The maximum bitrate gives the maximum rate in bits/second over any window
     * of one second.
     */
    var maxBitrate: Long = 0
        private set

    /**
     * The average bitrate gives the average rate in bits/second over the entire
     * presentation.
     */
    var averageBitrate: Long = 0
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        maxPDUsize = `in`.readBytes(2)
        averagePDUsize = `in`.readBytes(2)
        maxBitrate = `in`.readBytes(4)
        averageBitrate = `in`.readBytes(4)
        `in`.skipBytes(4) //reserved
    }
}
