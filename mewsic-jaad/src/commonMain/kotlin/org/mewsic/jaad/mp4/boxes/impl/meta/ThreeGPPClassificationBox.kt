package org.mewsic.jaad.mp4.boxes.impl.meta

import org.mewsic.jaad.mp4.MP4InputStream

class ThreeGPPClassificationBox :
    ThreeGPPMetadataBox("3GPP Classification Box") {
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
