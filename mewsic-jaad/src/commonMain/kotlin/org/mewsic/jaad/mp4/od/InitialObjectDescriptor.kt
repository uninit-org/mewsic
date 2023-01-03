package net.sourceforge.jaad.mp4.od

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The `InitialObjectDescriptor` is a variation of the
 * `ObjectDescriptor` that shall be used to gain initial access to
 * content.
 *
 * @author in-somnia
 */
class InitialObjectDescriptor : Descriptor() {
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
    private var includeInlineProfiles = false

    /**
     * A URL String that shall point to another InitialObjectDescriptor. If no
     * URL is present (if `isURLPresent()` returns false) this method
     * returns null.
     *
     * @return a URL String or null if none is present
     */
    var uRL: String? = null
        private set

    //TODO: javadoc
    var oDProfile = 0
        private set

    /**
     * An indication of the scene description profile required to process the
     * content associated with this InitialObjectDescriptor.<br></br>
     * The value should be one of the following:
     * 0x00: reserved for ISO use
     * 0x01: ISO 14496-1 XXXX profile
     * 0x02-0x7F: reserved for ISO use
     * 0x80-0xFD: user private
     * 0xFE: no scene description profile specified
     * 0xFF: no scene description capability required
     *
     * @return the scene profile
     */
    var sceneProfile = 0
        private set

    /**
     * An indication of the audio profile required to process the content
     * associated with this InitialObjectDescriptor.<br></br>
     * The value should be one of the following:
     * 0x00: reserved for ISO use
     * 0x01: ISO 14496-3 XXXX profile
     * 0x02-0x7F: reserved for ISO use
     * 0x80-0xFD: user private
     * 0xFE: no audio profile specified
     * 0xFF: no audio capability required
     *
     * @return the audio profile
     */
    var audioProfile = 0
        private set

    /**
     * An indication of the visual profile required to process the content
     * associated with this InitialObjectDescriptor.<br></br>
     * The value should be one of the following:
     * 0x00: reserved for ISO use
     * 0x01: ISO 14496-2 XXXX profile
     * 0x02-0x7F: reserved for ISO use
     * 0x80-0xFD: user private
     * 0xFE: no visual profile specified
     * 0xFF: no visual capability required
     *
     * @return the visual profile
     */
    var visualProfile = 0
        private set

    /**
     * An indication of the graphics profile required to process the content
     * associated with this InitialObjectDescriptor.<br></br>
     * The value should be one of the following:
     * 0x00: reserved for ISO use
     * 0x01: ISO 14496-1 XXXX profile
     * 0x02-0x7F: reserved for ISO use
     * 0x80-0xFD: user private
     * 0xFE: no graphics profile specified
     * 0xFF: no graphics capability required
     *
     * @return the graphics profile
     */
    var graphicsProfile = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        //10 bits objectDescriptorID, 1 bit url flag, 1 bit
        //includeInlineProfiles flag, 4 bits reserved
        val x = `in`.readBytes(2).toInt()
        objectDescriptorID = x shr 6 and 0x3FF
        isURLPresent = x shr 5 and 1 == 1
        includeInlineProfiles = x shr 4 and 1 == 1
        if (isURLPresent) uRL = `in`.readString(size - 2) else {
            oDProfile = `in`.read()
            sceneProfile = `in`.read()
            audioProfile = `in`.read()
            visualProfile = `in`.read()
            graphicsProfile = `in`.read()
        }
        readChildren(`in`)
    }

    /**
     * A flag that, if set, indicates that the subsequent profile indications
     * take into account the resources needed to process any content that may
     * be inlined.
     *
     * @return true if this ObjectDescriptor includes inline profiles
     */
    fun includesInlineProfiles(): Boolean {
        return includeInlineProfiles
    }

    /**
     * A flag that indicates the presence of profiles. If set, no URL is
     * present.
     *
     * @return true if profiles are present
     */
    fun areProfilesPresent(): Boolean {
        return !isURLPresent
    }
}
