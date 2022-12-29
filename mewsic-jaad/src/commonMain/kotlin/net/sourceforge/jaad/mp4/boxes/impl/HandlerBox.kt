package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * This box within a Media Box declares the process by which the media-data in
 * the track is presented, and thus, the nature of the media in a track. For
 * example, a video track would be handled by a video handler.
 *
 * This box when present within a Meta Box, declares the structure or format of
 * the 'meta' box contents.
 *
 * There is a general handler for metadata streams of any type; the specific
 * format is identified by the sample entry, as for video or audio, for example.
 * If they are in text, then a MIME format is supplied to document their format;
 * if in XML, each sample is a complete XML document, and the namespace of the
 * XML is also supplied.
 * @author in-somnia
 */
class HandlerBox : FullBox("Handler Box") {
    /**
     * When present in a media box, the handler type is an integer containing
     * one of the following values:
     *
     *  * 'vide': Video track
     *  * 'soun': Audio track
     *  * 'hint': Hint track
     *  * 'meta': Timed Metadata track
     *
     *
     * When present in a meta box, it contains an appropriate value to indicate
     * the format of the meta box contents. The value 'null' can be used in the
     * primary meta box to indicate that it is merely being used to hold
     * resources.
     *
     * @return the handler type
     */
    var handlerType: Long = 0
        private set

    /**
     * The name gives a human-readable name for the track type (for debugging
     * and inspection purposes).
     *
     * @return the handler type's name
     */
    var handlerName: String? = null
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        `in`.skipBytes(4) //pre-defined: 0
        handlerType = `in`.readBytes(4)
        `in`.readBytes(4) //reserved
        `in`.readBytes(4) //reserved
        `in`.readBytes(4) //reserved
        handlerName = `in`.readUTFString(getLeft(`in`) as Int, MP4InputStream.UTF8)
    }

    companion object {
        //ISO BMFF types
        const val TYPE_VIDEO = 1986618469 //vide
        const val TYPE_SOUND = 1936684398 //soun
        const val TYPE_HINT = 1751740020 //hint
        const val TYPE_META = 1835365473 //meta
        const val TYPE_NULL = 1853189228 //null

        //MP4 types
        const val TYPE_ODSM = 1868854125 //odsm
        const val TYPE_CRSM = 1668445037 //crsm
        const val TYPE_SDSM = 1935962989 //sdsm
        const val TYPE_M7SM = 1832350573 //m7sm
        const val TYPE_OCSM = 1868788589 //ocsm
        const val TYPE_IPSM = 1768977261 //ipsm
        const val TYPE_MJSM = 1835692909 //mjsm
    }
}
