package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

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
    private var color: java.awt.Color? = null
    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        graphicsMode = `in`.readBytes(2)
        //6 byte RGB color
        val c = IntArray(3)
        for (i in 0..2) {
            c[i] = `in`.read() and 0xFF or (`in`.read() shl 8 and 0xFF)
        }
        color = java.awt.Color(c[0], c[1], c[2])
    }

    /**
     * A color available for use by graphics modes.
     */
    fun getColor(): java.awt.Color? {
        return color
    }
}
