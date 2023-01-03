package org.mewsic.jaad.mp4.boxes.impl.oma

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

/**
 * The rights object box may be used to insert a Protected Rights Object,
 * defined in 'OMA DRM v2.1' section 5.3.9, into a DCF or PDCF. A Mutable DRM
 * Information box may include zero or more Rights Object boxes.
 *
 * @author in-somnia
 */
class OMARightsObjectBox : FullBox("OMA DRM Rights Object Box") {
    /**
     * Returns an array containing the rights object.
     *
     * @return a rights object
     */
    lateinit var data: ByteArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        data = ByteArray(getLeft(`in`).toInt())
        `in`.readBytes(data)
    }
}
