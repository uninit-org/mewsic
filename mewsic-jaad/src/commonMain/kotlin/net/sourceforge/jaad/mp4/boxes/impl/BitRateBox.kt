package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class BitRateBox : BoxImpl("Bitrate Box") {
    /**
     * Gives the size of the decoding buffer for the elementary stream in bytes.
     * @return the decoding buffer size
     */
    var decodingBufferSize: Long = 0
        private set

    /**
     * Gives the maximum rate in bits/second over any window of one second.
     * @return the maximum bitrate
     */
    var maximumBitrate: Long = 0
        private set

    /**
     * Gives the average rate in bits/second over the entire presentation.
     * @return the average bitrate
     */
    var averageBitrate: Long = 0
        private set

    @Throws(Exception::class)
    fun decode(`in`: MP4InputStream) {
        decodingBufferSize = `in`.readBytes(4)
        maximumBitrate = `in`.readBytes(4)
        averageBitrate = `in`.readBytes(4)
    }
}
