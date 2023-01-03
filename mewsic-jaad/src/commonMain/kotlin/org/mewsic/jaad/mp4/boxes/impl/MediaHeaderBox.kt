package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.Utils

/**
 * The media header declares overall information that is media-independent, and relevant to characteristics of
 * the media in a track.
 */
class MediaHeaderBox : FullBox("Media Header Box") {
    /**
     * The creation time is an integer that declares the creation time of the
     * presentation in seconds since midnight, Jan. 1, 1904, in UTC time.
     * @return the creation time
     */
    var creationTime: Long = 0
        private set

    /**
     * The modification time is an integer that declares the most recent time
     * the presentation was modified in seconds since midnight, Jan. 1, 1904,
     * in UTC time.
     */
    var modificationTime: Long = 0
        private set

    /**
     * The time-scale is an integer that specifies the time-scale for this
     * media; this is the number of time units that pass in one second. For
     * example, a time coordinate system that measures time in sixtieths of a
     * second has a time scale of 60.
     * @return the time-scale
     */
    var timeScale: Long = 0
        private set

    /**
     * The duration is an integer that declares the duration of this media (in
     * the scale of the timescale). If the duration cannot be determined then
     * duration is set to -1.
     * @return the duration of this media
     */
    var duration: Long = 0
        private set

    /**
     * Language code for this media as defined in ISO 639-2/T.
     * @return the language code
     */
    var language: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val len = if (version == 1) 8 else 4
        creationTime = `in`.readBytes(len)
        modificationTime = `in`.readBytes(len)
        timeScale = `in`.readBytes(4)
        duration = Utils.detectUndetermined(`in`.readBytes(len))
        language = Utils.getLanguageCode(`in`.readBytes(2))
        `in`.skipBytes(2) //pre-defined: 0
    }
}
