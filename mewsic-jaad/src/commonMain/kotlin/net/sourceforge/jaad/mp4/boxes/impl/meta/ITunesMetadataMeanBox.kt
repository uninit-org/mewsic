package net.sourceforge.jaad.mp4.boxes.impl.meta

import net.sourceforge.jaad.mp4.MP4InputStream

class ITunesMetadataMeanBox : FullBox("iTunes Metadata Mean Box") {
    var domain: String? = null
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        domain = `in`.readString(getLeft(`in`) as Int)
    }
}
