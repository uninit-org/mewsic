package net.sourceforge.jaad.mp4.boxes.impl
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox

/**
 * The video media header contains general presentation information, independent
 * of the coding, for video media
 * @author in-somnia
 */
class VideoMediaHeaderBox : FullBox("Video Media Header Box") {
    /**
     * The graphics mode specifies a composition mode for this video track.
     * Currently, only one mode is defined:
     * '0': copy over the existing image
     */
    var graphicsMode: Long = 0
        private set
    private var color: IntArray? = null
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        graphicsMode = `in`.readBytes(2)
        //6 byte RGB color
        val c = IntArray(3)
        for (i in 0..2) {
            c[i] = `in`.read() and 0xFF or (`in`.read() shl 8 and 0xFF)
        }
        color = c
    }

    /**
     * A color available for use by graphics modes.
     */
    fun getColor(): IntArray? {
        return color
    }
}
