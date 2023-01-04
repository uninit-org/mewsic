package org.mewsic.jaad.mp4.od

import org.mewsic.commons.lang.Log
import org.mewsic.jaad.mp4.MP4InputStream

/**
 * The abstract base class and factory for all descriptors (defined in ISO
 * 14496-1 as 'ObjectDescriptors').
 *
 * @author in-somnia
 */
abstract class Descriptor protected constructor() {
    //getter
    var type = 0
    var size = 0
    var start: Long = 0
    val children: MutableList<Descriptor>

    init {
        children = ArrayList<Descriptor>()
    }

    @Throws(Exception::class)
    abstract fun decode(`in`: MP4InputStream)

    //children
    @Throws(Exception::class)
    protected fun readChildren(`in`: MP4InputStream) {
        var desc: Descriptor
        while (size - (`in`.getOffset() - start) > 0) {
            desc = createDescriptor(`in`)
            children.add(desc)
        }
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
                Log.info("Descriptor: bytes left: $remaining, offset: ${`in`.getOffset()}, start: ${desc.start}, size: ${desc.size}")
                `in`.skipBytes(remaining)
            }
            desc.size += read //include type and size fields
            return desc
        }

        fun forTag(tag: Int): Descriptor {
            val desc: Descriptor
            when (tag) {
                TYPE_OBJECT_DESCRIPTOR -> desc = ObjectDescriptor()
                TYPE_INITIAL_OBJECT_DESCRIPTOR, TYPE_MP4_INITIAL_OBJECT_DESCRIPTOR -> desc =
                    InitialObjectDescriptor()

                TYPE_ES_DESCRIPTOR -> desc = ESDescriptor()
                TYPE_DECODER_CONFIG_DESCRIPTOR -> desc = DecoderConfigDescriptor()
                TYPE_DECODER_SPECIFIC_INFO -> desc = DecoderSpecificInfo()
                TYPE_SL_CONFIG_DESCRIPTOR -> {
//                    java.util.logging.Logger.getLogger("MP4 Boxes")
//                        .log(java.util.logging.Level.INFO, "Unknown descriptor type: {0}", tag)
                    desc = UnknownDescriptor()
                }

                else -> {
//                    java.util.logging.Logger.getLogger("MP4 Boxes")
//                        .log(java.util.logging.Level.INFO, "Unknown descriptor type: {0}", tag)
                    desc = UnknownDescriptor()
                }
            }
            return desc
        }
    }
}
