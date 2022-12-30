package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The Original Format Box contains the four-character-code of the original
 * un-transformed sample description.
 *
 * @author in-somnia
 */
class OriginalFormatBox : BoxImpl("Original Format Box") {
    /**
     * The original format is the four-character-code of the original
     * un-transformed sample entry (e.g. 'mp4v' if the stream contains protected
     * MPEG-4 visual material).
     *
     * @return the stream's original format
     */
    var originalFormat: Long = 0
        private set

    @Throws(Exception::class)
    fun decode(`in`: MP4InputStream) {
        originalFormat = `in`.readBytes(4)
    }
}
