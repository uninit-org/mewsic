package org.mewsic.jaad.mp4.boxes.impl.meta

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox
import org.mewsic.jaad.mp4.boxes.Utils

//TODO: use nio ByteBuffer instead of array
class ID3TagBox : FullBox("ID3 Tag Box") {
    /**
     * The language code for the following text. See ISO 639-2/T for the set of
     * three character codes.
     */
    var language: String? = null
        private set
    lateinit var iD3Data: ByteArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        language = Utils.getLanguageCode(`in`.readBytes(2))
        iD3Data = ByteArray(getLeft(`in`).toInt())
        `in`.readBytes(iD3Data)
    }
}
