package net.sourceforge.jaad.mp4.api

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.Box
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import net.sourceforge.jaad.mp4.boxes.impl.HandlerBox
import net.sourceforge.jaad.mp4.boxes.impl.ItemProtectionBox
import net.sourceforge.jaad.mp4.boxes.impl.MovieHeaderBox

class Movie(moov: Box, `in`: MP4InputStream) {
    private val `in`: MP4InputStream
    private val mvhd: MovieHeaderBox
    private val tracks: MutableList<Track>
    private val metaData: net.sourceforge.jaad.mp4.api.MetaData
    private val protections: MutableList<Protection>

    init {
        this.`in` = `in`

        //create tracks
        mvhd = moov.getChild(BoxTypes.MOVIE_HEADER_BOX) as MovieHeaderBox
        val trackBoxes: List<Box> = moov.getChildren(BoxTypes.TRACK_BOX)!!
        tracks = ArrayList<Track>(trackBoxes.size)
        var track: Track?
        for (i in trackBoxes.indices) {
            track = createTrack(trackBoxes[i])
            if (track != null) tracks.add(track)
        }

        //read metadata: moov.meta/moov.udta.meta
        metaData = net.sourceforge.jaad.mp4.api.MetaData()
        if (moov.hasChild(BoxTypes.META_BOX)) metaData.parse(
            null,
            moov.getChild(BoxTypes.META_BOX)!!
        ) else if (moov.hasChild(BoxTypes.USER_DATA_BOX)) {
            val udta: Box = moov.getChild(BoxTypes.USER_DATA_BOX)!!
            if (udta.hasChild(BoxTypes.META_BOX)) metaData.parse(udta, udta.getChild(BoxTypes.META_BOX)!!)
        }

        //detect DRM
        protections = ArrayList<Protection>()
        if (moov.hasChild(BoxTypes.ITEM_PROTECTION_BOX)) {
            val ipro: Box = moov.getChild(BoxTypes.ITEM_PROTECTION_BOX) as ItemProtectionBox
            for (sinf in ipro.getChildren(BoxTypes.PROTECTION_SCHEME_INFORMATION_BOX)!!) {
                protections.add(Protection.parse(sinf))
            }
        }
    }

    //TODO: support hint and meta
    private fun createTrack(trak: Box): Track? {
        val hdlr: HandlerBox = trak.getChild(BoxTypes.MEDIA_BOX)?.getChild(BoxTypes.HANDLER_BOX) as HandlerBox
        val track: Track?
        when (hdlr.handlerType.toInt()) {
            HandlerBox.TYPE_VIDEO -> track = net.sourceforge.jaad.mp4.api.VideoTrack(trak, `in`)
            HandlerBox.TYPE_SOUND -> track = AudioTrack(trak, `in`)
            else -> track = null
        }
        return track
    }

    /**
     * Returns an unmodifiable list of all tracks in this movie. The tracks are
     * ordered as they appeare in the file/stream.
     *
     * @return the tracks contained by this movie
     */
    fun getTracks(): List<Track> {
        return tracks.toList()
    }

    /**
     * Returns an unmodifiable list of all tracks in this movie with the
     * specified type. The tracks are ordered as they appeare in the
     * file/stream.
     *
     * @return the tracks contained by this movie with the passed type
     */
    fun getTracks(type: net.sourceforge.jaad.mp4.api.Type): List<Track> {
        val l: MutableList<Track> = ArrayList<Track>()
        for (t in tracks) {
            if (t.type == type) l.add(t)
        }
        return l.toList()
    }

    /**
     * Returns an unmodifiable list of all tracks in this movie whose samples
     * are encoded with the specified codec. The tracks are ordered as they
     * appeare in the file/stream.
     *
     * @return the tracks contained by this movie with the passed type
     */
    fun getTracks(codec: net.sourceforge.jaad.mp4.api.Track.Codec): List<Track> {
        val l: MutableList<Track> = ArrayList<Track>()
        for (t in tracks) {
            if (t.codec == codec) l.add(t)
        }
        return l.toList()
    }

    /**
     * Indicates if this movie contains metadata. If false the `MetaData`
     * object returned by `getMetaData()` will not contain any field.
     *
     * @return true if this movie contains any metadata
     */
    fun containsMetaData(): Boolean {
        return metaData.containsMetaData()
    }

    /**
     * Returns the MetaData object for this movie.
     *
     * @return the MetaData for this movie
     */
    fun getMetaData(): net.sourceforge.jaad.mp4.api.MetaData {
        return metaData
    }

    /**
     * Returns the `ProtectionInformation` objects that contains
     * details about the DRM systems used. If no protection is present the
     * returned list will be empty.
     *
     * @return a list of protection informations
     */
    fun getProtections(): List<Protection> {
        return protections.toList()
    }

    //mvhd
    val creationTime: Long
        /**
         * Returns the time this movie was created.
         * @return the creation time
         */
        get() = net.sourceforge.jaad.mp4.api.Utils.getDate(mvhd.creationTime)
    val modificationTime: Long
        /**
         * Returns the last time this movie was modified.
         * @return the modification time
         */
        get() = net.sourceforge.jaad.mp4.api.Utils.getDate(mvhd.modificationTime)
    val duration: Double
        /**
         * Returns the duration in seconds.
         * @return the duration
         */
        get() = (mvhd.duration  / mvhd.timeScale).toDouble()

    /**
     * Indicates if there are more frames to be read in this movie.
     *
     * @return true if there is at least one track in this movie that has at least one more frame to read.
     */
    fun hasMoreFrames(): Boolean {
        for (track in tracks) {
            if (track.hasMoreFrames()) return true
        }
        return false
    }

    /**
     * Reads the next frame from this movie (from one of the contained tracks).
     * The frame is the next in time-order, thus the next for playback. If none
     * of the tracks contains any more frames, null is returned.
     *
     * @return the next frame or null if there are no more frames to read from this movie.
     * @throws IOException if reading fails
     */
    @Throws(Exception::class)
    fun readNextFrame(): net.sourceforge.jaad.mp4.api.Frame? {
        var track: Track? = null
        for (t in tracks) {
            if (t.hasMoreFrames() && (track == null || t.nextTimeStamp < track.nextTimeStamp)) track = t
        }
        return track?.readNextFrame()
    }
}
