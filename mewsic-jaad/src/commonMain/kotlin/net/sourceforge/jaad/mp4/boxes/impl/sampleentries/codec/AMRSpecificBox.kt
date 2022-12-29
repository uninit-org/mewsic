package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec

import net.sourceforge.jaad.mp4.MP4InputStream

class AMRSpecificBox : net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox("AMR Specific Box") {
    var modeSet = 0
        private set
    var modeChangePeriod = 0
        private set
    var framesPerSample = 0
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        decodeCommon(`in`)
        modeSet = `in`.readBytes(2) as Int
        modeChangePeriod = `in`.read()
        framesPerSample = `in`.read()
    }
}
