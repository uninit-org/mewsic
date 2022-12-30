package net.sourceforge.jaad.mp4.boxes.impl
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

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
