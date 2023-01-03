package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec

import net.sourceforge.jaad.mp4.MP4InputStream

class H263SpecificBox : CodecSpecificBox("H.263 Specific Box") {
    var level = 0
        private set
    var profile = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        decodeCommon(`in`)
        level = `in`.read()
        profile = `in`.read()
    }
}
