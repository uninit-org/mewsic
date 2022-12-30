package net.sourceforge.jaad.mp4.boxes.impl.meta
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class RatingBox : FullBox("Rating Box") {
    var languageCode: String? = null
        private set
    var rating: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        //3gpp or iTunes
        if (parent.getType() === BoxTypes.USER_DATA_BOX) {
            super.decode(`in`)

            //TODO: what to do with both?
            val entity: Long = `in`.readBytes(4)
            val criteria: Long = `in`.readBytes(4)
            languageCode = Utils.getLanguageCode(`in`.readBytes(2))
            val b: ByteArray = `in`.readTerminated(getLeft(`in`) as Int, 0)
            rating = String(b, MP4InputStream.UTF8)
        } else readChildren(`in`)
    }
}
