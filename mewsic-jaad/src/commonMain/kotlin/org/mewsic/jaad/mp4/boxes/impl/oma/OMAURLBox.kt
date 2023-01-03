package org.mewsic.jaad.mp4.boxes.impl.oma

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

/**
 * This box is used for several sub-boxes of the user-data box in an OMA DRM
 * file. These boxes have in common, that they only contain one String.
 *
 * @author in-somnia
 */
class OMAURLBox(name: String) : FullBox(name) {
    /**
     * Returns the String that this box contains. Its meaning depends on the
     * type of this box.
     *
     * @return the content of this box
     */
    var content: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val b = ByteArray(getLeft(`in`).toInt())
        `in`.readBytes(b)
        content = b.decodeToString()
    }
}
