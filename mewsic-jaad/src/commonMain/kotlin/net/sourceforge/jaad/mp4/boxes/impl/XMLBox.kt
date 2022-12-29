package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * When the primary data is in XML format and it is desired that the XML be
 * stored directly in the meta-box, either the XMLBox or the BinaryXMLBox is
 * used. The Binary XML Box may only be used when there is a single well-defined
 * binarization of the XML for that defined format as identified by the handler.
 *
 * @see BinaryXMLBox
 *
 * @author in-somnia
 */
class XMLBox : FullBox("XML Box") {
    /**
     * The XML content.
     */
    var content: String? = null
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        content = `in`.readUTFString(getLeft(`in`) as Int)
    }
}
