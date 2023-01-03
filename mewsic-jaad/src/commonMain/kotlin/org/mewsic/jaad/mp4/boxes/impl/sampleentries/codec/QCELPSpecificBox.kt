package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec

import net.sourceforge.jaad.mp4.MP4InputStream

class QCELPSpecificBox :
    CodecSpecificBox("QCELP Specific Box") {
    var framesPerSample = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        decodeCommon(`in`)
        framesPerSample = `in`.read()
    }
}
