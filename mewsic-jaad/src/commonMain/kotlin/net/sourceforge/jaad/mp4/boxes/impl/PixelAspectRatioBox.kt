package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class PixelAspectRatioBox : BoxImpl("Pixel Aspect Ratio Box") {
    var horizontalSpacing: Long = 0
        private set
    var verticalSpacing: Long = 0
        private set

    @Throws(Exception::class)
    fun decode(`in`: MP4InputStream) {
        horizontalSpacing = `in`.readBytes(4)
        verticalSpacing = `in`.readBytes(4)
    }
}
