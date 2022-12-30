package net.sourceforge.jaad.mp4.boxes
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * Box implementation that is used for unknown types.
 *
 * @author in-somnia
 */
internal class UnknownBox : net.sourceforge.jaad.mp4.boxes.BoxImpl("unknown") {
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        //no need to read, box will be skipped
    }
}
