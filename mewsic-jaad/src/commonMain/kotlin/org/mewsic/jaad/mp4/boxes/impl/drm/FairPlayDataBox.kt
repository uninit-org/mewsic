package net.sourceforge.jaad.mp4.boxes.impl.drm

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.BoxImpl

class FairPlayDataBox : BoxImpl("iTunes FairPlay Data Box") {
    lateinit var data: ByteArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        data = ByteArray(getLeft(`in`).toInt())
        `in`.readBytes(data)
    }
}
