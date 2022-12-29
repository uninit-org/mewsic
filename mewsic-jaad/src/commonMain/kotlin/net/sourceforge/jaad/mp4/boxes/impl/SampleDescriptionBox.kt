package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The sample description table gives detailed information about the coding type
 * used, and any initialization information needed for that coding.
 * @author in-somnia
 */
class SampleDescriptionBox : FullBox("Sample Description Box") {
    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4) as Int
        readChildren(`in`, entryCount)
    }
}
