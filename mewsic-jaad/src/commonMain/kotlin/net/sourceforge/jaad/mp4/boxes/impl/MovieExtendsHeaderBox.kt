package net.sourceforge.jaad.mp4.boxes.impl
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The Movie Extends Header is optional, and provides the overall duration,
 * including fragments, of a fragmented movie. If this box is not present, the
 * overall duration must be computed by examining each fragment.
 *
 * @author in-somnia
 */
class MovieExtendsHeaderBox : FullBox("Movie Extends Header Box") {
    /**
     * The fragment duration is an integer that declares length of the
     * presentation of the whole movie including fragments (in the timescale
     * indicated in the Movie Header Box). The value of this field corresponds
     * to the duration of the longest track, including movie fragments. If an
     * MP4 file is created in real-time, such as used in live streaming, it is
     * not likely that the fragment duration is known in advance and this box
     * may be omitted.
     *
     * @return the fragment duration
     */
    var fragmentDuration: Long = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val len = if (version === 1) 8 else 4
        fragmentDuration = `in`.readBytes(len)
    }
}
