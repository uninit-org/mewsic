package org.mewsic.jaad.mp4.boxes.impl.meta

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.BoxTypes
import org.mewsic.jaad.mp4.boxes.FullBox
import org.mewsic.jaad.mp4.boxes.Utils

class GenreBox : FullBox("Genre Box") {
    var languageCode: String? = null
        private set
    var genre: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        //3gpp or iTunes
        if (parent?.type == BoxTypes.USER_DATA_BOX) {
            super.decode(`in`)
            languageCode = Utils.getLanguageCode(`in`.readBytes(2))
            val b: ByteArray = `in`.readTerminated(getLeft(`in`).toInt(), 0)
            genre = b.decodeToString()
        } else readChildren(`in`)
    }
}
