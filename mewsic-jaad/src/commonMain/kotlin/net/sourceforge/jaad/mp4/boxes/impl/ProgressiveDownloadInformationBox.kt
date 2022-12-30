package net.sourceforge.jaad.mp4.boxes.impl
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The Progressive download information box aids the progressive download of an
 * ISO file. The box contains pairs of numbers (to the end of the box)
 * specifying combinations of effective file download bitrate in units of
 * bytes/sec and a suggested initial playback delay in units of milliseconds.
 *
 * The download rate can be estimated from the download rate and obtain an upper
 * estimate for a suitable initial delay by linear interpolation between pairs,
 * or by extrapolation from the first or last entry.
 * @author in-somnia
 */
class ProgressiveDownloadInformationBox : FullBox("Progressive Download Information Box") {
    private val pairs: MutableMap<Long, Long>

    init {
        pairs = HashMap<Long, Long>()
    }

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        var rate: Long
        var initialDelay: Long
        while (getLeft(`in`) > 0) {
            rate = `in`.readBytes(4)
            initialDelay = `in`.readBytes(4)
            pairs[rate] = initialDelay
        }
    }

    val informationPairs: Map<Long, Long>
        /**
         * The map contains pairs of bitrates and playback delay.
         * @return the information pairs
         */
        get() = pairs
}
