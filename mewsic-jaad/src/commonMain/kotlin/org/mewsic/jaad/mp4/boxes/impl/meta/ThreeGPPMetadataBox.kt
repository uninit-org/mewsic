package org.mewsic.jaad.mp4.boxes.impl.meta

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox
import org.mewsic.jaad.mp4.boxes.Utils

open class ThreeGPPMetadataBox(name: String) : FullBox(name) {
    /**
     * The language code for the following text. See ISO 639-2/T for the set of
     * three character codes.
     */
    var languageCode: String? = null
        private set
    var data: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        decodeCommon(`in`)
        data = `in`.readUTFString(getLeft(`in`).toInt())
    }

    //called directly by subboxes that don't contain the 'data' string
    @Throws(Exception::class)
    protected fun decodeCommon(`in`: MP4InputStream) {
        super.decode(`in`)
        languageCode = Utils.getLanguageCode(`in`.readBytes(2))
    }
}
