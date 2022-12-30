package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox

/**
 * The Scheme Type Box identifies the protection scheme.
 *
 * @author in-somnia
 */
class SchemeTypeBox : FullBox("Scheme Type Box") {
    /**
     * The scheme type is the code defining the protection scheme.
     *
     * @return the scheme type
     */
    var schemeType: Long = 0
        private set

    /**
     * The scheme version is the version of the scheme used to create the
     * content.
     *
     * @return the scheme version
     */
    var schemeVersion: Long = 0
        private set

    /**
     * The optional scheme URI allows for the option of directing the user to a
     * web-page if they do not have the scheme installed on their system. It is
     * an absolute URI.
     * If the scheme URI is not present, this method returns null.
     *
     * @return the scheme URI or null, if no URI is present
     */
    var schemeURI: String? = null
        private set

    @Throws(Exception::class)
    override override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        schemeType = `in`.readBytes(4)
        schemeVersion = `in`.readBytes(4)
        schemeURI = if (flags and 1 == 1) `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8) else null
    }

    companion object {
        const val ITUNES_SCHEME: Long = 1769239918 //itun
    }
}
