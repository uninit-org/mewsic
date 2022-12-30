package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class H263SpecificBox : net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox("H.263 Specific Box") {
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
