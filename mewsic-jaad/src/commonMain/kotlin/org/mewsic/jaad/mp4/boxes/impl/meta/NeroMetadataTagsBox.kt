package net.sourceforge.jaad.mp4.boxes.impl.meta

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.BoxImpl

class NeroMetadataTagsBox : BoxImpl("Nero Metadata Tags Box") {
    private val pairs: MutableMap<String, String>

    init {
        pairs = HashMap<String, String>()
    }

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        `in`.skipBytes(12) //meta box
        var key: String
        var `val`: String
        var len: Int
        //TODO: what are the other skipped fields for?
        while (getLeft(`in`) > 0 && `in`.read() == 0x80) {
            `in`.skipBytes(2) //x80 x00 x06/x05
            key = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
            `in`.skipBytes(4) //0x00 0x01 0x00 0x00 0x00
            len = `in`.read()
            `val` = `in`.readString(len)
            pairs[key] = `val`
        }
    }

    fun getPairs(): Map<String, String> {
        return pairs
    }
}
