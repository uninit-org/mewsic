package org.mewsic.jaad.mp4.boxes.impl.meta

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.BoxTypes
import org.mewsic.jaad.mp4.boxes.FullBox
import org.mewsic.jaad.mp4.boxes.Utils

class RatingBox : FullBox("Rating Box") {
    var languageCode: String? = null
        private set
    var rating: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        //3gpp or iTunes
        if (parent?.type == BoxTypes.USER_DATA_BOX) {
            super.decode(`in`)

            //TODO: what to do with both?
            val entity: Long = `in`.readBytes(4)
            val criteria: Long = `in`.readBytes(4)
            languageCode = Utils.getLanguageCode(`in`.readBytes(2))
            val b: ByteArray = `in`.readTerminated(getLeft(`in`).toInt(), 0)
            rating = b.decodeToString()
        } else readChildren(`in`)
    }
}
