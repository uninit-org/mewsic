package net.sourceforge.jaad.mp4.boxes.impl.meta
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class ThreeGPPAlbumBox : net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox("3GPP Album Box") {
    /**
     * The track number (order number) of the media on this album. This is an
     * optional field. If the field is not present, -1 is returned.
     *
     * @return the track number
     */
    var trackNumber = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        trackNumber = if (getLeft(`in`) > 0) `in`.read() else -1
    }
}
