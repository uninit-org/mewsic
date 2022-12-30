package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class CleanApertureBox : BoxImpl("Clean Aperture Box") {
    var cleanApertureWidthN: Long = 0
        private set
    var cleanApertureWidthD: Long = 0
        private set
    var cleanApertureHeightN: Long = 0
        private set
    var cleanApertureHeightD: Long = 0
        private set
    var horizOffN: Long = 0
        private set
    var horizOffD: Long = 0
        private set
    var vertOffN: Long = 0
        private set
    var vertOffD: Long = 0
        private set

    @Throws(Exception::class)
    fun decode(`in`: MP4InputStream) {
        cleanApertureWidthN = `in`.readBytes(4)
        cleanApertureWidthD = `in`.readBytes(4)
        cleanApertureHeightN = `in`.readBytes(4)
        cleanApertureHeightD = `in`.readBytes(4)
        horizOffN = `in`.readBytes(4)
        horizOffD = `in`.readBytes(4)
        vertOffN = `in`.readBytes(4)
        vertOffD = `in`.readBytes(4)
    }
}
