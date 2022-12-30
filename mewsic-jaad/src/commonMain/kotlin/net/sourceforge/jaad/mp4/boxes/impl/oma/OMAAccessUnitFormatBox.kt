package net.sourceforge.jaad.mp4.boxes.impl.oma
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class OMAAccessUnitFormatBox : FullBox("OMA DRM Access Unit Format Box") {
    var isSelectiveEncrypted = false
        private set
    var keyIndicatorLength = 0
        private set
    var initialVectorLength = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)

        //1 bit selective encryption, 7 bits reserved
        isSelectiveEncrypted = `in`.read() shr 7 and 1 === 1
        keyIndicatorLength = `in`.read() //always zero?
        initialVectorLength = `in`.read()
    }
}
