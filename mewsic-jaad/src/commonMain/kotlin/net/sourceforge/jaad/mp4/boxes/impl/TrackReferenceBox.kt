package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The Track Reference Box provides a reference from the containing track to
 * another track in the presentation. These references are typed. A 'hint'
 * reference links from the containing hint track to the media data that it
 * hints. A content description reference 'cdsc' links a descriptive or
 * metadata track to the content which it describes.
 *
 * Exactly one Track Reference Box can be contained within the Track Box.
 *
 * If this box is not present, the track is not referencing any other track in
 * any way. The reference array is sized to fill the reference type box.
 * @author in-somnia
 */
class TrackReferenceBox : BoxImpl("Track Reference Box") {
    /**
     * The reference type shall be set to one of the following values:
     *
     *  * 'hint': the referenced track(s) contain the original media for this
     * hint track.
     *  * 'cdsc': this track describes the referenced track.
     *  * 'hind': this track depends on the referenced hint track, i.e., it
     * should only be used if the referenced hint track is used.
     * @return the reference type
     */
    var referenceType: String? = null
        private set
    private val trackIDs: MutableList<Long>

    init {
        trackIDs = java.util.ArrayList<Long>()
    }

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        referenceType = `in`.readString(4)
        while (getLeft(`in`) > 3) {
            trackIDs.add(`in`.readBytes(4))
        }
    }

    /**
     * The track IDs are integers that provide a reference from the containing
     * track to other tracks in the presentation. Track IDs are never re-used
     * and cannot be equal to zero.
     * @return the track IDs this box refers to
     */
    fun getTrackIDs(): List<Long> {
        return java.util.Collections.unmodifiableList<Long>(trackIDs)
    }
}
