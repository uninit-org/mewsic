package net.sourceforge.jaad.mp4.od

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
class ObjectDescriptor : net.sourceforge.jaad.mp4.od.Descriptor() {
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
        val x = `in`.readBytes(2) as Int
        objectDescriptorID = x shr 6 and 0x3FF
        isURLPresent = x shr 5 and 1 == 1
        if (isURLPresent) uRL = `in`.readString(size - 2)
        readChildren(`in`)
    }
}
