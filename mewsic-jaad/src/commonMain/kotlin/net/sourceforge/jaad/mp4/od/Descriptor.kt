package net.sourceforge.jaad.mp4.od

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The abstract base class and factory for all descriptors (defined in ISO
 * 14496-1 as 'ObjectDescriptors').
 *
 * @author in-somnia
 */
abstract class Descriptor protected constructor() {
    //getter
    var type = 0
        protected set
    var size = 0
        protected set
    protected var start: Long = 0
    private val children: MutableList<Descriptor>

    init {
        children = java.util.ArrayList<Descriptor>()
    }

    @Throws(java.io.IOException::class)
    abstract fun decode(`in`: MP4InputStream?)

    //children
    @Throws(java.io.IOException::class)
    protected fun readChildren(`in`: MP4InputStream) {
        var desc: Descriptor
        while (size - (`in`.getOffset() - start) > 0) {
            desc = createDescriptor(`in`)
            children.add(desc)
        }
    }

    fun getChildren(): List<Descriptor> {
        return java.util.Collections.unmodifiableList<Descriptor>(children)
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
        @Throws(java.io.IOException::class)
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
                java.util.logging.Logger.getLogger("MP4 Boxes").log(
                    java.util.logging.Level.INFO,
                    "Descriptor: bytes left: {0}, offset: {1}",
                    arrayOf(remaining, `in`.getOffset())
                )
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
                    java.util.logging.Logger.getLogger("MP4 Boxes")
                        .log(java.util.logging.Level.INFO, "Unknown descriptor type: {0}", tag)
                    desc = net.sourceforge.jaad.mp4.od.UnknownDescriptor()
                }

                else -> {
                    java.util.logging.Logger.getLogger("MP4 Boxes")
                        .log(java.util.logging.Level.INFO, "Unknown descriptor type: {0}", tag)
                    desc = net.sourceforge.jaad.mp4.od.UnknownDescriptor()
                }
            }
            return desc
        }
    }
}
