package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

/**
 * The chapter box allows to specify individual chapters along the main timeline
 * of a movie. The chapter box occurs within a movie box.
 * Defined in "Adobe Video File Format Specification v10".
 *
 * @author in-somnia
 */
class ChapterBox : FullBox("Chapter Box") {
    private val chapters: MutableMap<Long, String>

    init {
        chapters = HashMap<Long, String>()
    }

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        `in`.skipBytes(4) //??
        val count: Int = `in`.read()
        var timestamp: Long
        var len: Int
        var name: String
        for (i in 0 until count) {
            timestamp = `in`.readBytes(8)
            len = `in`.read()
            name = `in`.readString(len)
            chapters[timestamp] = name
        }
    }

    /**
     * Returns a map that maps the timestamp of each chapter to its name.
     *
     * @return the chapters
     */
    fun getChapters(): Map<Long, String> {
        return chapters
    }
}
