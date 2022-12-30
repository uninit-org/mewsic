package net.sourceforge.jaad.mp4.boxes.impl.oma
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * This box is used for several sub-boxes of the user-data box in an OMA DRM
 * file. These boxes have in common, that they only contain one String.
 *
 * @author in-somnia
 */
class OMAURLBox(name: String?) : FullBox(name) {
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
        val b = ByteArray(getLeft(`in`) as Int)
        `in`.readBytes(b)
        content = String(b, MP4InputStream.UTF8)
    }
}
