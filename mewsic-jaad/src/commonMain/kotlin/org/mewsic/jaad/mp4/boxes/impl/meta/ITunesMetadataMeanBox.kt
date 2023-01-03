package net.sourceforge.jaad.mp4.boxes.impl.meta

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox

class ITunesMetadataMeanBox : FullBox("iTunes Metadata Mean Box") {
    var domain: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        domain = `in`.readString(getLeft(`in`).toInt())
    }
}
