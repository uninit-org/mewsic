package net.sourceforge.jaad.mp4.boxes.impl.meta

import net.sourceforge.jaad.mp4.MP4InputStream

class ThreeGPPRecordingYearBox : FullBox("3GPP Recording Year Box") {
    var year = 0
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        year = `in`.readBytes(2) as Int
    }
}
