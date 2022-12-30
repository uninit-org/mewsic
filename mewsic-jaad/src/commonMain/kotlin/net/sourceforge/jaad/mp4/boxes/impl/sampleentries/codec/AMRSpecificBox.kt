package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class AMRSpecificBox : net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox("AMR Specific Box") {
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
