package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The Copyright box contains a copyright declaration which applies to the
 * entire presentation, when contained within the Movie Box, or, when contained
 * in a track, to that entire track. There may be multiple copyright boxes using
 * different language codes.
 */
class CopyrightBox : FullBox("Copyright Box") {
    /**
     * The language code for the following text. See ISO 639-2/T for the set of
     * three character codes.
     */
    var languageCode: String? = null
        private set

    /**
     * The copyright notice.
     */
    var notice: String? = null
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        if (parent.getType() === BoxTypes.USER_DATA_BOX) {
            super.decode(`in`)
            //1 bit padding, 5*3 bits language code (ISO-639-2/T)
            languageCode = Utils.getLanguageCode(`in`.readBytes(2))
            notice = `in`.readUTFString(getLeft(`in`) as Int)
        } else if (parent.getType() === BoxTypes.ITUNES_META_LIST_BOX) readChildren(`in`)
    }
}
