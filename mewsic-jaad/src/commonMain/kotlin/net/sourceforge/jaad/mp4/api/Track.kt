package net.sourceforge.jaad.mp4.api

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * This class represents a track in a movie.
 *
 * Each track contains either a decoder specific info as a byte array or a
 * `DecoderInfo` object that contains necessary information for the
 * decoder.
 *
 * @author in-somnia
 */
//TODO: expand javadoc; use generics for subclasses?
abstract class Track internal constructor(trak: Box, `in`: MP4InputStream) {
    interface Codec { //TODO: currently only marker interface
    }

    private val `in`: MP4InputStream
    protected val tkhd: TrackHeaderBox
    private val mdhd: MediaHeaderBox

    /**
     * Returns true if the data for this track is present in this file (stream).
     * If not, `getLocation()` returns the URL where the data can be
     * found.
     * @return true if the data is in this file (stream), false otherwise
     */
    var isInFile = false
    private var frames: List<net.sourceforge.jaad.mp4.api.Frame>? = null
    private var location: java.net.URL? = null
    private var currentFrame: Int

    //info structures
    protected var decoderSpecificInfo: DecoderSpecificInfo? = null
    protected var decoderInfo: DecoderInfo? = null
    protected var protection: Protection? = null

    init {
        this.`in` = `in`
        tkhd = trak.getChild(BoxTypes.TRACK_HEADER_BOX) as TrackHeaderBox
        val mdia: Box = trak.getChild(BoxTypes.MEDIA_BOX)
        mdhd = mdia.getChild(BoxTypes.MEDIA_HEADER_BOX) as MediaHeaderBox
        val minf: Box = mdia.getChild(BoxTypes.MEDIA_INFORMATION_BOX)
        val dinf: Box = minf.getChild(BoxTypes.DATA_INFORMATION_BOX)
        val dref: DataReferenceBox = dinf.getChild(BoxTypes.DATA_REFERENCE_BOX) as DataReferenceBox
        //TODO: support URNs
        if (dref.hasChild(BoxTypes.DATA_ENTRY_URL_BOX)) {
            val url: DataEntryUrlBox = dref.getChild(BoxTypes.DATA_ENTRY_URL_BOX) as DataEntryUrlBox
            isInFile = url.isInFile()
            if (!isInFile) {
                location = try {
                    java.net.URL(url.getLocation())
                } catch (e: java.net.MalformedURLException) {
                    java.util.logging.Logger.getLogger("MP4 API").log(
                        java.util.logging.Level.WARNING,
                        "Parsing URL-Box failed: {0}, url: {1}",
                        arrayOf<String>(e.toString(), url.getLocation())
                    )
                    null
                }
            }
        } else {
            isInFile = true
            location = null
        }

        //sample table
        val stbl: Box = minf.getChild(BoxTypes.SAMPLE_TABLE_BOX)
        if (stbl.hasChildren()) {
            frames = java.util.ArrayList<net.sourceforge.jaad.mp4.api.Frame>()
            parseSampleTable(stbl)
        } else frames = emptyList<net.sourceforge.jaad.mp4.api.Frame>()
        currentFrame = 0
    }

    private fun parseSampleTable(stbl: Box) {
        val timeScale: Double = mdhd.getTimeScale()
        val type: net.sourceforge.jaad.mp4.api.Type = type

        //sample sizes
        val sampleSizes: LongArray = (stbl.getChild(BoxTypes.SAMPLE_SIZE_BOX) as SampleSizeBox).getSampleSizes()

        //chunk offsets
        val stco: ChunkOffsetBox
        stco =
            if (stbl.hasChild(BoxTypes.CHUNK_OFFSET_BOX)) stbl.getChild(BoxTypes.CHUNK_OFFSET_BOX) as ChunkOffsetBox else stbl.getChild(
                BoxTypes.CHUNK_LARGE_OFFSET_BOX
            ) as ChunkOffsetBox
        val chunkOffsets: LongArray = stco.getChunks()

        //samples to chunks
        val stsc: SampleToChunkBox = stbl.getChild(BoxTypes.SAMPLE_TO_CHUNK_BOX) as SampleToChunkBox
        val firstChunks: LongArray = stsc.getFirstChunks()
        val samplesPerChunk: LongArray = stsc.getSamplesPerChunk()

        //sample durations/timestamps
        val stts: DecodingTimeToSampleBox =
            stbl.getChild(BoxTypes.DECODING_TIME_TO_SAMPLE_BOX) as DecodingTimeToSampleBox
        val sampleCounts: LongArray = stts.getSampleCounts()
        val sampleDeltas: LongArray = stts.getSampleDeltas()
        val timeOffsets = LongArray(sampleSizes.size)
        var tmp: Long = 0
        var off = 0
        for (i in sampleCounts.indices) {
            for (j in 0 until sampleCounts[i]) {
                timeOffsets[(off + j).toInt()] = tmp
                tmp += sampleDeltas[i]
            }
            off += sampleCounts[i].toInt()
        }

        //create samples
        var current = 0
        var lastChunk: Int
        var timeStamp: Double
        var offset: Long = 0
        //iterate over all chunk groups
        for (i in firstChunks.indices) {
            lastChunk = if (i < firstChunks.size - 1) firstChunks[i + 1].toInt() - 1 else chunkOffsets.size

            //iterate over all chunks in current group
            for (j in firstChunks[i].toInt() - 1 until lastChunk) {
                offset = chunkOffsets[j]

                //iterate over all samples in current chunk
                for (k in 0 until samplesPerChunk[i]) {
                    //create samples
                    timeStamp = timeOffsets[current].toDouble() / timeScale
                    frames.add(net.sourceforge.jaad.mp4.api.Frame(type, offset, sampleSizes[current], timeStamp))
                    offset += sampleSizes[current]
                    current++
                }
            }
        }

        //frames need not to be time-ordered: sort by timestamp
        //TODO: is it possible to add them to the specific position?
        java.util.Collections.sort<net.sourceforge.jaad.mp4.api.Frame>(frames)
    }

    //TODO: implement other entry descriptors
    protected fun findDecoderSpecificInfo(esds: ESDBox) {
        val ed: Descriptor = esds.getEntryDescriptor()
        val children: List<Descriptor> = ed.getChildren()
        var children2: List<Descriptor?>
        for (e in children) {
            children2 = e.getChildren()
            for (e2 in children2) {
                when (e2.getType()) {
                    Descriptor.TYPE_DECODER_SPECIFIC_INFO -> decoderSpecificInfo = e2 as DecoderSpecificInfo
                }
            }
        }
    }

    protected fun <T> parseSampleEntry(sampleEntry: Box, clazz: java.lang.Class<T>) {
        val type: T
        try {
            type = clazz.newInstance()
            if (sampleEntry.getClass().isInstance(type)) {
                println("true")
            }
        } catch (ex: java.lang.InstantiationException) {
            ex.printStackTrace()
        } catch (ex: java.lang.IllegalAccessException) {
            ex.printStackTrace()
        }
    }

    abstract val type: net.sourceforge.jaad.mp4.api.Type
    abstract val codec: Codec

    //tkhd
    val isEnabled: Boolean
        /**
         * Returns true if the track is enabled. A disabled track is treated as if
         * it were not present.
         * @return true if the track is enabled
         */
        get() = tkhd.isTrackEnabled()
    val isUsed: Boolean
        /**
         * Returns true if the track is used in the presentation.
         * @return true if the track is used
         */
        get() = tkhd.isTrackInMovie()
    val isUsedForPreview: Boolean
        /**
         * Returns true if the track is used in previews.
         * @return true if the track is used in previews
         */
        get() = tkhd.isTrackInPreview()
    val creationTime: java.util.Date
        /**
         * Returns the time this track was created.
         * @return the creation time
         */
        get() = net.sourceforge.jaad.mp4.api.Utils.getDate(tkhd.getCreationTime())
    val modificationTime: java.util.Date
        /**
         * Returns the last time this track was modified.
         * @return the modification time
         */
        get() = net.sourceforge.jaad.mp4.api.Utils.getDate(tkhd.getModificationTime())

    //mdhd
    val language: java.util.Locale
        /**
         * Returns the language for this media.
         * @return the language
         */
        get() = java.util.Locale(mdhd.getLanguage())

    /**
     * If the data for this track is not present in this file (if
     * `isInFile` returns false), this method returns the data's
     * location. Else null is returned.
     * @return the data's location or null if the data is in this file
     */
    fun getLocation(): java.net.URL? {
        return location
    }
    //info structures
    /**
     * Returns the decoder specific info, if present. It contains configuration
     * data for the decoder. If the decoder specific info is not present, the
     * track contains a `DecoderInfo`.
     *
     * @see .getDecoderInfo
     * @return the decoder specific info
     */
    fun getDecoderSpecificInfo(): ByteArray {
        return decoderSpecificInfo.getData()
    }

    /**
     * Returns the `DecoderInfo`, if present. It contains
     * configuration information for the decoder. If the structure is not
     * present, the track contains a decoder specific info.
     *
     * @see .getDecoderSpecificInfo
     * @return the codec specific structure
     */
    fun getDecoderInfo(): DecoderInfo? {
        return decoderInfo
    }

    /**
     * Returns the `ProtectionInformation` object that contains
     * details about the DRM system used. If no protection is present this
     * method returns null.
     *
     * @return a `ProtectionInformation` object or null if no
     * protection is used
     */
    fun getProtection(): Protection? {
        return protection
    }
    //reading
    /**
     * Indicates if there are more frames to be read in this track.
     *
     * @return true if there is at least one more frame to read.
     */
    fun hasMoreFrames(): Boolean {
        return currentFrame < frames!!.size
    }

    /**
     * Reads the next frame from this track. If it contains no more frames to
     * read, null is returned.
     *
     * @return the next frame or null if there are no more frames to read
     * @throws IOException if reading fails
     */
    @Throws(java.io.IOException::class)
    fun readNextFrame(): net.sourceforge.jaad.mp4.api.Frame? {
        var frame: net.sourceforge.jaad.mp4.api.Frame? = null
        if (hasMoreFrames()) {
            frame = frames!![currentFrame]
            val diff: Long = frame.getOffset() - `in`.getOffset()
            if (diff > 0) `in`.skipBytes(diff) else if (diff < 0) {
                if (`in`.hasRandomAccess()) `in`.seek(frame.getOffset()) else {
                    java.util.logging.Logger.getLogger("MP4 API").log(
                        java.util.logging.Level.WARNING,
                        "readNextFrame failed: frame {0} already skipped, offset:{1}, stream:{2}",
                        arrayOf<Any>(currentFrame, frame.getOffset(), `in`.getOffset())
                    )
                    throw java.io.IOException("frame already skipped and no random access")
                }
            }
            val b = ByteArray(frame.getSize().toInt())
            try {
                `in`.readBytes(b)
            } catch (e: java.io.EOFException) {
                java.util.logging.Logger.getLogger("MP4 API").log(
                    java.util.logging.Level.WARNING,
                    "readNextFrame failed: tried to read {0} bytes at {1}",
                    arrayOf<Long>(frame.getSize(), `in`.getOffset())
                )
                throw e
            }
            frame.setData(b)
            currentFrame++
        }
        return frame
    }

    /**
     * This method tries to seek to the frame that is nearest to the given
     * timestamp. It returns the timestamp of the frame it seeked to or -1 if
     * none was found.
     *
     * @param timestamp a timestamp to seek to
     * @return the frame's timestamp that the method seeked to
     */
    fun seek(timestamp: Double): Double {
        //find first frame > timestamp
        var frame: net.sourceforge.jaad.mp4.api.Frame? = null
        var i = 0
        while (i < frames!!.size) {
            frame = frames!![i++]
            if (frame.getTime() > timestamp) {
                currentFrame = i
                break
            }
            i++
        }
        return if (frame == null) -1 else frame.getTime()
    }

    val nextTimeStamp: Double
        /**
         * Returns the timestamp of the next frame to be read. This is needed to
         * read frames from a movie that contains multiple tracks.
         *
         * @return the next frame's timestamp
         */
        get() = frames!![currentFrame].getTime()
}
