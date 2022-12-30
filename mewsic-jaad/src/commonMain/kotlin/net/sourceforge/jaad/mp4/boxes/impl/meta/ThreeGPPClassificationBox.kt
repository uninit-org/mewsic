package net.sourceforge.jaad.mp4.boxes.impl.meta
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class ThreeGPPClassificationBox :
    net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox("3GPP Classification Box") {
    var entity: Long = 0
        private set
    var table = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        decodeCommon(`in`)
        entity = `in`.readBytes(4)
        table = `in`.readBytes(2).toInt()
    }
}
