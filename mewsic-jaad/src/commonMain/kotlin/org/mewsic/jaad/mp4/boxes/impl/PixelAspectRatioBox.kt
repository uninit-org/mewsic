package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.BoxImpl

class PixelAspectRatioBox : BoxImpl("Pixel Aspect Ratio Box") {
    var horizontalSpacing: Long = 0
        private set
    var verticalSpacing: Long = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        horizontalSpacing = `in`.readBytes(4)
        verticalSpacing = `in`.readBytes(4)
    }
}
