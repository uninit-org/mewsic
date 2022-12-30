package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * For a given handler, the primary data may be one of the referenced items when
 * it is desired that it be stored elsewhere, or divided into extents; or the
 * primary metadata may be contained in the meta-box (e.g. in an XML box).
 *
 * Either this box must occur, or there must be a box within the meta-box (e.g.
 * an XML box) containing the primary information in the format required by the
 * identified handler.
 *
 * @author in-somnia
 */
class PrimaryItemBox : FullBox("Primary Item Box") {
    /**
     * The item ID is the identifier of the primary item.
     *
     * @return the item ID
     */
    var itemID = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        itemID = `in`.readBytes(2) as Int
    }
}
