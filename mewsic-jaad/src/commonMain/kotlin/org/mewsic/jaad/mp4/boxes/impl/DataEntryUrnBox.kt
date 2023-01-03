package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox

class DataEntryUrnBox : FullBox("Data Entry Urn Box") {
    var isInFile = false
        private set
    var referenceName: String? = null
        private set
    var location: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        isInFile = flags and 1 == 1
        if (!isInFile) {
            referenceName = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
            if (getLeft(`in`) > 0) location = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
        }
    }
}
