package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * When the primary data is in XML format and it is desired that the XML be
 * stored directly in the meta-box, either the XMLBox or the BinaryXMLBox is
 * used. The Binary XML Box may only be used when there is a single well-defined
 * binarization of the XML for that defined format as identified by the handler.
 *
 * @see XMLBox
 *
 * @author in-somnia
 */
class BinaryXMLBox : FullBox("Binary XML Box") {
    /**
     * The binary data.
     */
    var data: ByteArray
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        data = ByteArray(getLeft(`in`) as Int)
        `in`.readBytes(data)
    }
}
