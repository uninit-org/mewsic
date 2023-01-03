package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox

class DataEntryUrlBox : FullBox("Data Entry Url Box") {
    var isInFile = false
        private set
    var location: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        isInFile = flags and 1 === 1
        if (!isInFile) location = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
    }
}
