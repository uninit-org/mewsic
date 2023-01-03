package org.mewsic.jaad.mp4.boxes.impl.meta

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.BoxTypes
import org.mewsic.jaad.mp4.boxes.FullBox

class EncoderBox : FullBox("Encoder Box") {
    var data: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        if (parent?.type == BoxTypes.ITUNES_META_LIST_BOX) readChildren(`in`) else {
            super.decode(`in`)
            data = `in`.readString(getLeft(`in`).toInt())
        }
    }
}
