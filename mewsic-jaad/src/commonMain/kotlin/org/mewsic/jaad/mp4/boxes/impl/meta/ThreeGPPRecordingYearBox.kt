package net.sourceforge.jaad.mp4.boxes.impl.meta

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox

class ThreeGPPRecordingYearBox : FullBox("3GPP Recording Year Box") {
    var year = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        year = `in`.readBytes(2).toInt()
    }
}
