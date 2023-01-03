package net.sourceforge.jaad.mp4.api

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.Box
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import net.sourceforge.jaad.mp4.boxes.impl.*
import net.sourceforge.jaad.mp4.od.DecoderSpecificInfo
import net.sourceforge.jaad.mp4.od.Descriptor

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
    private var frames: MutableList<Frame>? = null
    private var location: String? = null
    private var currentFrame: Int

    //info structures
    protected var decoderSpecificInfo: DecoderSpecificInfo? = null
    protected var decoderInfo: DecoderInfo? = null
    protected var protection: Protection? = null

    init {
        this.`in` = `in`
        tkhd = trak.getChild(BoxTypes.TRACK_HEADER_BOX) as TrackHeaderBox
        val mdia: Box = trak.getChild(BoxTypes.MEDIA_BOX)!!
        mdhd = mdia.getChild(BoxTypes.MEDIA_HEADER_BOX) as MediaHeaderBox
        val minf: Box = mdia.getChild(BoxTypes.MEDIA_INFORMATION_BOX) as MediaHeaderBox
        val dinf: Box = minf.getChild(BoxTypes.DATA_INFORMATION_BOX)!!
        val dref: DataReferenceBox = dinf.getChild(BoxTypes.DATA_REFERENCE_BOX) as DataReferenceBox
        //TODO: support URNs
        if (dref.hasChild(BoxTypes.DATA_ENTRY_URL_BOX)) {
            val url: DataEntryUrlBox = dref.getChild(BoxTypes.DATA_ENTRY_URL_BOX) as DataEntryUrlBox
            isInFile = url.isInFile
            if (!isInFile) {
                location = try {
                    url.location
                } catch (e: Exception) {
//                    java.util.logging.Logger.getLogger("MP4 API").log(
//                        java.util.logging.Level.WARNING,
//                        "Parsing URL-Box failed: {0}, url: {1}",
//                        arrayOf<String>(e.toString(), url.getLocation())
//                    )
                    null
                }
            }
        } else {
            isInFile = true
            location = null
        }

        //sample table
        val stbl: Box = minf.getChild(BoxTypes.SAMPLE_TABLE_BOX)!!
        if (stbl.hasChildren()) {
            frames = ArrayList<Frame>()
            parseSampleTable(stbl)
        } else frames = mutableListOf<Frame>()
        currentFrame = 0
    }

    private fun parseSampleTable(stbl: Box) {
        val timeScale: Long = mdhd.timeScale
        val type: Type = type

        //sample sizes
        val sampleSizes: LongArray = (stbl.getChild(BoxTypes.SAMPLE_SIZE_BOX) as SampleSizeBox).sampleSizes

        //chunk offsets
        val stco =
            if (stbl.hasChild(BoxTypes.CHUNK_OFFSET_BOX)) stbl.getChild(BoxTypes.CHUNK_OFFSET_BOX) as ChunkOffsetBox else stbl.getChild(
                BoxTypes.CHUNK_LARGE_OFFSET_BOX
            ) as ChunkOffsetBox
        val chunkOffsets: LongArray = stco.chunks

        //samples to chunks
        val stsc: SampleToChunkBox = stbl.getChild(BoxTypes.SAMPLE_TO_CHUNK_BOX) as SampleToChunkBox
        val firstChunks: LongArray = stsc.firstChunks
        val samplesPerChunk: LongArray = stsc.samplesPerChunk

        //sample durations/timestamps
        val stts: DecodingTimeToSampleBox =
            stbl.getChild(BoxTypes.DECODING_TIME_TO_SAMPLE_BOX) as DecodingTimeToSampleBox
        val sampleCounts: LongArray = stts.sampleCounts
        val sampleDeltas: LongArray = stts.sampleDeltas
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
                    frames?.add(Frame(type, offset, sampleSizes[current], timeStamp))
                    offset += sampleSizes[current]
                    current++
                }
            }
        }

        //frames need not to be time-ordered: sort by timestamp
        //TODO: is it possible to add them to the specific position?
//        java.util.Collections.sort<net.sourceforge.jaad.mp4.api.Frame>(frames)
        frames?.sortBy { it.offset } // FIXME: Check if this is correct
    }

    //TODO: implement other entry descriptors
    protected fun findDecoderSpecificInfo(esds: ESDBox) {
        val ed: Descriptor = esds.entryDescriptor!!
        val children: List<Descriptor> = ed.getChildren()
        var children2: List<Descriptor?>
        for (e in children) {
            children2 = e.getChildren()
            for (e2 in children2) {
                when (e2.type) {
                    Descriptor.TYPE_DECODER_SPECIFIC_INFO -> decoderSpecificInfo = e2 as DecoderSpecificInfo
                }
            }
        }
    }

    @Deprecated("Unused, only not removed just incase it's used somewhere right now. Will be removed in the future.")
    protected fun <T> parseSampleEntry(sampleEntry: Box, clazz: Any) {

    }

    abstract val type: Type
    abstract val codec: Codec

    //tkhd
    val isEnabled: Boolean
        /**
         * Returns true if the track is enabled. A disabled track is treated as if
         * it were not present.
         * @return true if the track is enabled
         */
        get() = tkhd.isTrackEnabled
    val isUsed: Boolean
        /**
         * Returns true if the track is used in the presentation.
         * @return true if the track is used
         */
        get() = tkhd.isTrackInMovie
    val isUsedForPreview: Boolean
        /**
         * Returns true if the track is used in previews.
         * @return true if the track is used in previews
         */
        get() = tkhd.isTrackInPreview
    val creationTime: Long
        /**
         * Returns the time this track was created.
         * @return the creation time
         */
        get() = Utils.getDate(tkhd.creationTime)
    val modificationTime: Long
        /**
         * Returns the last time this track was modified.
         * @return the modification time
         */
        get() = Utils.getDate(tkhd.modificationTime)

    //mdhd
    val language: String
        /**
         * Returns the language for this media.
         * @return the language
         */
        get() = mdhd.language ?: "und"

    /**
     * If the data for this track is not present in this file (if
     * `isInFile` returns false), this method returns the data's
     * location. Else null is returned.
     * @return the data's location or null if the data is in this file
     */
    fun getLocation(): String {
        return location ?: ""
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
        return decoderSpecificInfo!!.data
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
    @Throws(Exception::class)
    fun readNextFrame(): Frame? {
        var frame: Frame? = null
        if (hasMoreFrames()) {
            frame = frames!![currentFrame]
            val diff: Long = frame.offset - `in`.getOffset()
            if (diff > 0) `in`.skipBytes(diff) else if (diff < 0) {
                if (`in`.hasRandomAccess()) `in`.seek(frame.offset) else {
//                    java.util.logging.Logger.getLogger("MP4 API").log(
//                        java.util.logging.Level.WARNING,
//                        "readNextFrame failed: frame {0} already skipped, offset:{1}, stream:{2}",
//                        arrayOf<Any>(currentFrame, frame.getOffset(), `in`.getOffset())
//                    )
                    throw Exception("frame already skipped and no random access")
                }
            }
            val b = ByteArray(frame.size.toInt())
            try {
                `in`.readBytes(b)
            } catch (e: Exception) {
//                java.util.logging.Logger.getLogger("MP4 API").log(
//                    java.util.logging.Level.WARNING,
//                    "readNextFrame failed: tried to read {0} bytes at {1}",
//                    arrayOf<Long>(frame.getSize(), `in`.getOffset())
//                )
                throw e
            }
            frame.data = b
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
        var frame: Frame? = null
        var i = 0
        while (i < frames!!.size) {
            frame = frames!![i++]
            if (frame.time > timestamp) {
                currentFrame = i
                break
            }
            i++
        }
        return if (frame == null) -1.0 else frame.time
    }

    val nextTimeStamp: Double
        /**
         * Returns the timestamp of the next frame to be read. This is needed to
         * read frames from a movie that contains multiple tracks.
         *
         * @return the next frame's timestamp
         */
        get() = frames!![currentFrame].time
}
