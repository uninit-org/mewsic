package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The item protection box provides an array of item protection information, for
 * use by the Item Information Box.
 *
 * @author in-somnia
 */
class ItemProtectionBox : FullBox("Item Protection Box") {
    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val protectionCount = `in`.readBytes(2) as Int
        readChildren(`in`, protectionCount)
    }
}
