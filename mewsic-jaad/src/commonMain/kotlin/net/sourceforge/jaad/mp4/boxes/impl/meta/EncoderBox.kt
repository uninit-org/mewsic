package net.sourceforge.jaad.mp4.boxes.impl.meta
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class EncoderBox : FullBox("Encoder Box") {
    var data: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        if (parent?.type  == BoxTypes.ITUNES_META_LIST_BOX) readChildren(`in`) else {
            super.decode(`in`)
            data = `in`.readString(getLeft(`in`).toInt())
        }
    }
}
