package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

class DataEntryUrlBox : FullBox("Data Entry Url Box") {
    var isInFile = false
        private set
    var location: String? = null
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        isInFile = flags and 1 === 1
        if (!isInFile) location = `in`.readUTFString(getLeft(`in`) as Int, MP4InputStream.UTF8)
    }
}
