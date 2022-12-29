package net.sourceforge.jaad.mp4.boxes.impl.meta

import net.sourceforge.jaad.mp4.MP4InputStream

class GenreBox : FullBox("Genre Box") {
    var languageCode: String? = null
        private set
    var genre: String? = null
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        //3gpp or iTunes
        if (parent.getType() === BoxTypes.USER_DATA_BOX) {
            super.decode(`in`)
            languageCode = Utils.getLanguageCode(`in`.readBytes(2))
            val b: ByteArray = `in`.readTerminated(getLeft(`in`) as Int, 0)
            genre = String(b, MP4InputStream.UTF8)
        } else readChildren(`in`)
    }
}
