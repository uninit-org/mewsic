package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * This class is used for all boxes, that are known but don't contain necessary
 * data and can be skipped. This is mainly used for 'skip', 'free' and 'wide'.
 *
 * @author in-somnia
 */
class FreeSpaceBox : BoxImpl("Free Space Box") {
    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream?) {
        //no need to read, box will be skipped
    }
}
