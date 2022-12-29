package net.sourceforge.jaad.mp4.boxes.impl.drm

import net.sourceforge.jaad.mp4.MP4InputStream

class FairPlayDataBox : BoxImpl("iTunes FairPlay Data Box") {
    var data: ByteArray
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        data = ByteArray(getLeft(`in`) as Int)
        `in`.readBytes(data)
    }
}
