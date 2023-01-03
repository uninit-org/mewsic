package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

/**
 * The item protection box provides an array of item protection information, for
 * use by the Item Information Box.
 *
 * @author in-somnia
 */
class ItemProtectionBox : FullBox("Item Protection Box") {
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val protectionCount = `in`.readBytes(2).toInt()
        readChildren(`in`, protectionCount)
    }
}
