package net.sourceforge.jaad.mp4.boxes.impl.meta
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class ITunesMetadataMeanBox : FullBox("iTunes Metadata Mean Box") {
    var domain: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        domain = `in`.readString(getLeft(`in`).toInt())
    }
}
