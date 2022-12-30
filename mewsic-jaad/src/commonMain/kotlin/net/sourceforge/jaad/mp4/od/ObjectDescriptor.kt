package net.sourceforge.jaad.mp4.od
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The `ObjectDescriptor` consists of three different parts:
 *
 * The first part uniquely labels the `ObjectDescriptor` within its
 * name scope by means of an ID. Media objects in the scene description use this
 * ID to refer to their object descriptor. An optional URL String indicates that
 * the actual object descriptor resides at a remote location.
 *
 * The second part is a set of optional descriptors that support the inclusion
 * if future extensions as well as the transport of private data in a backward
 * compatible way.
 *
 * The third part consists of a list of `ESDescriptors`, each
 * providing parameters for a single elementary stream that relates to the media
 * object as well as an optional set of object content information descriptors.
 *
 * @author in-somnia
 */
open class ObjectDescriptor : net.sourceforge.jaad.mp4.od.Descriptor() {
    /**
     * The ID uniquely identifies this ObjectDescriptor within its name scope.
     * It should be within 0 and 1023 exclusively. The value 0 is forbidden and
     * the value 1023 is reserved.
     *
     * @return this ObjectDescriptor's ID
     */
    var objectDescriptorID = 0
        private set

    /**
     * A flag that indicates the presence of a URL. If set, no profiles are
     * present.
     *
     * @return true if a URL is present
     */
    var isURLPresent = false
        private set

    /**
     * A URL String that shall point to another InitialObjectDescriptor. If no
     * URL is present (if `isURLPresent()` returns false) this method
     * returns null.
     *
     * @return a URL String or null if none is present
     */
    var uRL: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        //10 bits objectDescriptorID, 1 bit url flag, 5 bits reserved
        val x = `in`.readBytes(2).toInt()
        objectDescriptorID = x shr 6 and 0x3FF
        isURLPresent = x shr 5 and 1 == 1
        if (isURLPresent) uRL = `in`.readString(size - 2)
        readChildren(`in`)
    }
    companion object {
        const val TYPE_OBJECT_DESCRIPTOR = 1
        const val TYPE_INITIAL_OBJECT_DESCRIPTOR = 2
        const val TYPE_ES_DESCRIPTOR = 3
        const val TYPE_DECODER_CONFIG_DESCRIPTOR = 4
        const val TYPE_DECODER_SPECIFIC_INFO = 5
        const val TYPE_SL_CONFIG_DESCRIPTOR = 6
        const val TYPE_ES_ID_INC = 14
        const val TYPE_MP4_INITIAL_OBJECT_DESCRIPTOR = 16
        @Throws(Exception::class)
        fun createDescriptor(`in`: MP4InputStream): Descriptor {
            //read tag and size
            val type: Int = `in`.read()
            var read = 1
            var size = 0
            var b = 0
            do {
                b = `in`.read()
                size = size shl 7
                size = size or (b and 0x7f)
                read++
            } while (b and 0x80 == 0x80)

            //create descriptor
            val desc = forTag(type)
            desc.type = type
            desc.size = size
            desc.start = `in`.getOffset()

            //decode
            desc.decode(`in`)
            //skip remaining bytes
            val remaining: Long = size - (`in`.getOffset() - desc.start)
            if (remaining > 0) {
//                java.util.logging.Logger.getLogger("MP4 Boxes").log(
//                    java.util.logging.Level.INFO,
//                    "Descriptor: bytes left: {0}, offset: {1}",
//                    arrayOf(remaining, `in`.getOffset())
//                )
                `in`.skipBytes(remaining)
            }
            desc.size += read //include type and size fields
            return desc
        }

        private fun forTag(tag: Int): Descriptor {
            val desc: Descriptor
            when (tag) {
                TYPE_OBJECT_DESCRIPTOR -> desc = net.sourceforge.jaad.mp4.od.ObjectDescriptor()
                TYPE_INITIAL_OBJECT_DESCRIPTOR, TYPE_MP4_INITIAL_OBJECT_DESCRIPTOR -> desc =
                    net.sourceforge.jaad.mp4.od.InitialObjectDescriptor()

                TYPE_ES_DESCRIPTOR -> desc = net.sourceforge.jaad.mp4.od.ESDescriptor()
                TYPE_DECODER_CONFIG_DESCRIPTOR -> desc = net.sourceforge.jaad.mp4.od.DecoderConfigDescriptor()
                TYPE_DECODER_SPECIFIC_INFO -> desc = net.sourceforge.jaad.mp4.od.DecoderSpecificInfo()
                TYPE_SL_CONFIG_DESCRIPTOR -> {
//                    java.util.logging.Logger.getLogger("MP4 Boxes")
//                        .log(java.util.logging.Level.INFO, "Unknown descriptor type: {0}", tag)
                    desc = net.sourceforge.jaad.mp4.od.UnknownDescriptor()
                }

                else -> {
//                    java.util.logging.Logger.getLogger("MP4 Boxes")
//                        .log(java.util.logging.Level.INFO, "Unknown descriptor type: {0}", tag)
                    desc = net.sourceforge.jaad.mp4.od.UnknownDescriptor()
                }
            }
            return desc
        }
    }

}
