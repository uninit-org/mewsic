package net.sourceforge.jaad.mp4.boxes

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * Box implementation that is used for unknown types.
 *
 * @author in-somnia
 */
internal class UnknownBox : net.sourceforge.jaad.mp4.boxes.BoxImpl("unknown") {
    @Throws(java.io.IOException::class)
    override fun decode(`in`: MP4InputStream?) {
        //no need to read, box will be skipped
    }
}
