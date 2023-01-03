package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

/**
 * The sample description table gives detailed information about the coding type
 * used, and any initialization information needed for that coding.
 * @author in-somnia
 */
class SampleDescriptionBox : FullBox("Sample Description Box") {
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4).toInt()
        readChildren(`in`, entryCount)
    }
}
