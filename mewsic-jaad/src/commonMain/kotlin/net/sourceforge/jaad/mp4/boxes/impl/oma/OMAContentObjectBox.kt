package net.sourceforge.jaad.mp4.boxes.impl.oma

import net.sourceforge.jaad.mp4.MP4InputStream

class OMAContentObjectBox : FullBox("OMA Content Object Box") {
    /**
     * Returns the data of this content object.
     *
     * @return the data
     */
    var data: ByteArray
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val len = `in`.readBytes(4) as Int
        data = ByteArray(len)
        `in`.readBytes(data)
    }
}
