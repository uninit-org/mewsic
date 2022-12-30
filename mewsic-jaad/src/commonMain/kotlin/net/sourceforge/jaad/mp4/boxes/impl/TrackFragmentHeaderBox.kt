package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * Each movie fragment can add zero or more fragments to each track; and a track
 * fragment can add zero or more contiguous runs of samples. The track fragment
 * header sets up information and defaults used for those runs of samples.
 *
 * @author in-somnia
 */
class TrackFragmentHeaderBox : FullBox("Track Fragment Header Box") {
    var trackID: Long = 0
        private set
    var isBaseDataOffsetPresent = false
        private set
    var isSampleDescriptionIndexPresent = false
        private set
    var isDefaultSampleDurationPresent = false
        private set
    var isDefaultSampleSizePresent = false
        private set
    var isDefaultSampleFlagsPresent = false
        private set
    var isDurationIsEmpty = false
        private set
    var baseDataOffset: Long = 0
        private set
    var sampleDescriptionIndex: Long = 0
        private set
    var defaultSampleDuration: Long = 0
        private set
    var defaultSampleSize: Long = 0
        private set
    var defaultSampleFlags: Long = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        trackID = `in`.readBytes(4)

        //optional fields
        isBaseDataOffsetPresent = flags and 1 === 1
        baseDataOffset = if (isBaseDataOffsetPresent) `in`.readBytes(8) else 0
        isSampleDescriptionIndexPresent = flags and 2 === 2
        sampleDescriptionIndex = if (isSampleDescriptionIndexPresent) `in`.readBytes(4) else 0
        isDefaultSampleDurationPresent = flags and 8 === 8
        defaultSampleDuration = if (isDefaultSampleDurationPresent) `in`.readBytes(4) else 0
        isDefaultSampleSizePresent = flags and 16 === 16
        defaultSampleSize = if (isDefaultSampleSizePresent) `in`.readBytes(4) else 0
        isDefaultSampleFlagsPresent = flags and 32 === 32
        defaultSampleFlags = if (isDefaultSampleFlagsPresent) `in`.readBytes(4) else 0
        isDurationIsEmpty = flags and 0x10000 === 0x10000
    }
}
