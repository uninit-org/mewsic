package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec

import net.sourceforge.jaad.mp4.MP4InputStream

class AMRSpecificBox : CodecSpecificBox("AMR Specific Box") {
    var modeSet = 0
        private set
    var modeChangePeriod = 0
        private set
    var framesPerSample = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        decodeCommon(`in`)
        modeSet = `in`.readBytes(2).toInt()
        modeChangePeriod = `in`.read()
        framesPerSample = `in`.read()
    }
}
