package org.mewsic.jaad.mp4.boxes.impl.oma

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

/**
 * The Discrete Media headers box includes fields specific to the DCF format and
 * the Common Headers box, followed by an optional user-data box. There must be
 * exactly one OMADiscreteHeaders box in a single OMA DRM Container box, as the
 * first box in the container.
 *
 * @author in-somnia
 */
class OMADiscreteMediaHeadersBox : FullBox("OMA DRM Discrete Media Headers Box") {
    /**
     * The content type indicates the original MIME media type of the Content
     * Object i.e. what content type the result of a successful extraction of
     * the OMAContentBox represents.
     *
     * @return the content type
     */
    var contentType: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val len: Int = `in`.read()
        contentType = `in`.readString(len)
        readChildren(`in`)
    }
}
