package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.boxes.FullBox

/**
 * The movie header box defines overall information which is media-independent,
 * and relevant to the entire presentation considered as a whole.
 * @author in-somnia
 */
class MovieHeaderBox : FullBox("Movie Header Box") {
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
     * The time-scale is an integer that specifies the time-scale for the entire
     * presentation; this is the number of time units that pass in one second.
     * For example, a time coordinate system that measures time in sixtieths of
     * a second has a time scale of 60.
     * @return the time-scale
     */
    var timeScale: Long = 0
        private set

    /**
     * The duration is an integer that declares length of the presentation (in
     * the indicated timescale). This property is derived from the
     * presentation's tracks: the value of this field corresponds to the
     * duration of the longest track in the presentation. If the duration cannot
     * be determined then duration is set to -1.
     * @return the duration of the longest track
     */
    var duration: Long = 0
        private set

    /**
     * The rate is a floting point number that indicates the preferred rate
     * to play the presentation; 1.0 is normal forward playback
     * @return the playback rate
     */
    var rate = 0.0
        private set

    /**
     * The volume is a floating point number that indicates the preferred
     * playback volume: 0.0 is mute, 1.0 is normal volume.
     * @return the volume
     */
    var volume = 0.0
        private set

    /**
     * Provides a transformation matrix for the video:
     * [A,B,U,C,D,V,X,Y,W]
     * A: width scale
     * B: width rotate
     * U: width angle
     * C: height rotate
     * D: height scale
     * V: height angle
     * X: position from left
     * Y: position from top
     * W: divider scale (restricted to 1.0)
     *
     * The normal values for scale are 1.0 and for rotate 0.0.
     * The angles are restricted to 0.0.
     *
     * @return the transformation matrix for the video
     */
    val transformationMatrix: DoubleArray

    /**
     * The next-track-ID is a non-zero integer that indicates a value to use
     * for the track ID of the next track to be added to this presentation. Zero
     * is not a valid track ID value. The value shall be larger than the largest
     * track-ID in use. If this value is equal to all 1s (32-bit), and a new
     * media track is to be added, then a search must be made in the file for an
     * unused track identifier.
     * @return the ID for the next track
     */
    var nextTrackID: Long = 0
        private set

    init {
        transformationMatrix = DoubleArray(9)
    }

    @Throws(java.io.IOException::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val len = if (version === 1) 8 else 4
        creationTime = `in`.readBytes(len)
        modificationTime = `in`.readBytes(len)
        timeScale = `in`.readBytes(4)
        duration = Utils.detectUndetermined(`in`.readBytes(len))
        rate = `in`.readFixedPoint(16, 16)
        volume = `in`.readFixedPoint(8, 8)
        `in`.skipBytes(10) //reserved
        for (i in 0..8) {
            if (i < 6) transformationMatrix[i] = `in`.readFixedPoint(16, 16) else transformationMatrix[i] =
                `in`.readFixedPoint(2, 30)
        }
        `in`.skipBytes(24) //reserved
        nextTrackID = `in`.readBytes(4)
    }
}
