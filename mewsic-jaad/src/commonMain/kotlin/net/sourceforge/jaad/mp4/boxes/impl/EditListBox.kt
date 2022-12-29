package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * This box contains an explicit timeline map. Each entry defines part of the
 * track time-line: by mapping part of the media time-line, or by indicating
 * 'empty' time, or by defining a 'dwell', where a single time-point in the
 * media is held for a period.
 *
 * Starting offsets for tracks (streams) are represented by an initial empty
 * edit. For example, to play a track from its start for 30 seconds, but at 10
 * seconds into the presentation, we have the following edit list:
 *
 * [0]:
 * Segment-duration = 10 seconds
 * Media-Time = -1
 * Media-Rate = 1
 *
 * [1]:
 * Segment-duration = 30 seconds (could be the length of the whole track)
 * Media-Time = 0 seconds
 * Media-Rate = 1
 */
class EditListBox : FullBox("Edit List Box") {
    /**
     * The segment duration is an integer that specifies the duration of this
     * edit segment in units of the timescale in the Movie Header Box.
     */
    var segmentDuration: LongArray
        private set

    /**
     * The media time is an integer containing the starting time within the
     * media of a specific edit segment (in media time scale units, in
     * composition time). If this field is set to –1, it is an empty edit. The
     * last edit in a track shall never be an empty edit. Any difference between
     * the duration in the Movie Header Box, and the track's duration is
     * expressed as an implicit empty edit at the end.
     */
    var mediaTime: LongArray
        private set

    /**
     * The media rate specifies the relative rate at which to play the media
     * corresponding to a specific edit segment. If this value is 0, then the
     * edit is specifying a ‘dwell’: the media at media-time is presented for the
     * segment-duration. Otherwise this field shall contain the value 1.
     */
    var mediaRate: DoubleArray
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4) as Int
        val len = if (version === 1) 8 else 4
        segmentDuration = LongArray(entryCount)
        mediaTime = LongArray(entryCount)
        mediaRate = DoubleArray(entryCount)
        for (i in 0 until entryCount) {
            segmentDuration[i] = `in`.readBytes(len)
            mediaTime[i] = `in`.readBytes(len)

            //int(16) mediaRate_integer;
            //int(16) media_rate_fraction = 0;
            mediaRate[i] = `in`.readFixedPoint(16, 16)
        }
    }
}
