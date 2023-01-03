package org.mewsic.jaad.mp4.boxes

import org.mewsic.jaad.mp4.MP4InputStream

/**
 * Box implementation that is used for unknown types.
 *
 * @author in-somnia
 */
internal class UnknownBox : BoxImpl("unknown") {
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        //no need to read, box will be skipped
    }
}
