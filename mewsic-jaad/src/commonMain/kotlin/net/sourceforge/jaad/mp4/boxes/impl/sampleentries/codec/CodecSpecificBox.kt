package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.BoxImpl

/**
 * The `CodecSpecificBox` can be used instead of an `ESDBox`
 * in a sample entry. It contains `DecoderSpecificInfo`s.
 *
 * @author in-somnia
 */
abstract class CodecSpecificBox(name: String) : BoxImpl(name) {
    var vendor: Long = 0
        private set
    var decoderVersion = 0
        private set

    @Throws(Exception::class)
    protected fun decodeCommon(`in`: MP4InputStream) {
        vendor = `in`.readBytes(4)
        decoderVersion = `in`.read()
    }
}
