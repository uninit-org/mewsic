package net.sourceforge.jaad.mp4.boxes.impl
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * This class is used for all boxes, that are known but don't contain necessary
 * data and can be skipped. This is mainly used for 'skip', 'free' and 'wide'.
 *
 * @author in-somnia
 */
class FreeSpaceBox : BoxImpl("Free Space Box") {
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        //no need to read, box will be skipped
    }
}
