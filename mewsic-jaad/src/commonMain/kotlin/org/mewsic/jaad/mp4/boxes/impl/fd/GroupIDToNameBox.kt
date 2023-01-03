package org.mewsic.jaad.mp4.boxes.impl.fd

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

class GroupIDToNameBox : FullBox("Group ID To Name Box") {
    private val map: MutableMap<Long, String>

    init {
        map = HashMap<Long, String>()
    }

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(2).toInt()
        var id: Long
        var name: String
        for (i in 0 until entryCount) {
            id = `in`.readBytes(4)
            name = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
            map[id] = name
        }
    }

    /**
     * Returns the map that contains the ID-name-pairs for all groups.
     *
     * @return the ID to name map
     */
    fun getMap(): Map<Long, String> {
        return map
    }
}
