package net.sourceforge.jaad.mp4.od

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The `DecoderSpecificInfo` constitutes an opaque container with
 * information for a specific media decoder. Depending on the required amout of
 * data, two classes with a maximum of 255 and 2<sup>32</sup>-1 bytes of data
 * are provided. The existence and semantics of the
 * `DecoderSpecificInfo` depends on the stream type and object
 * profile of the parent `DecoderConfigDescriptor`.
 *
 * @author in-somnia
 */
class DecoderSpecificInfo : Descriptor() {
    /**
     * A byte array containing the decoder specific information.
     *
     * @return the decoder specific information
     */
    lateinit var data: ByteArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        data = ByteArray(size)
        `in`.readBytes(data)
    }
}
