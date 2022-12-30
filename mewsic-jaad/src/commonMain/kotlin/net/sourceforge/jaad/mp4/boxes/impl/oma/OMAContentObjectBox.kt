package net.sourceforge.jaad.mp4.boxes.impl.oma
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class OMAContentObjectBox : FullBox("OMA Content Object Box") {
    /**
     * Returns the data of this content object.
     *
     * @return the data
     */
    lateinit var data: ByteArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val len = `in`.readBytes(4) as Int
        data = ByteArray(len)
        `in`.readBytes(data)
    }
}
