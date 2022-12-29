package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec

import net.sourceforge.jaad.mp4.MP4InputStream

class SMVSpecificBox :
    net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox("SMV Specific Structure") {
    var framesPerSample = 0
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        decodeCommon(`in`)
        framesPerSample = `in`.read()
    }
}
