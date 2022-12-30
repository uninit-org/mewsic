package net.sourceforge.jaad.mp4.boxes.impl.meta
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.Utils

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
