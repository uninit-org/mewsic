package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The movie fragment header contains a sequence number, as a safety check. The
 * sequence number usually starts at 1 and must increase for each movie fragment
 * in the file, in the order in which they occur. This allows readers to verify
 * integrity of the sequence; it is an error to construct a file where the
 * fragments are out of sequence.
 */
class MovieFragmentHeaderBox : FullBox("Movie Fragment Header Box") {
    /**
     * The ordinal number of this fragment, in increasing order.
     */
    var sequenceNumber: Long = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        sequenceNumber = `in`.readBytes(4)
    }
}
