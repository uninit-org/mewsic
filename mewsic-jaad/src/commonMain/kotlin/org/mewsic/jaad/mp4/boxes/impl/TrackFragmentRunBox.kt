package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

/**
 * Within the Track Fragment Box, there are zero or more Track Run Boxes. If the
 * duration-is-empty flag is set in the track fragment box, there are no track
 * runs. A track run documents a contiguous set of samples for a track.
 *
 * If the data-offset is not present, then the data for this run starts
 * immediately after the data of the previous run, or at the base-data-offset
 * defined by the track fragment header if this is the first run in a track
 * fragment.
 * If the data-offset is present, it is relative to the base-data-offset
 * established in the track fragment header.
 *
 * @author in-somnia
 */
class TrackFragmentRunBox : FullBox("Track Fragment Run Box") {
    var sampleCount = 0
        private set
    var isDataOffsetPresent = false
        private set
    var isFirstSampleFlagsPresent = false
        private set
    var dataOffset: Long = 0
        private set
    var firstSampleFlags: Long = 0
        private set
    var isSampleDurationPresent = false
        private set
    var isSampleSizePresent = false
        private set
    var isSampleFlagsPresent = false
        private set
    var isSampleCompositionTimeOffsetPresent = false
        private set
    lateinit var sampleDuration: LongArray
        private set
    lateinit var sampleSize: LongArray
        private set
    lateinit var sampleFlags: LongArray
        private set
    lateinit var sampleCompositionTimeOffset: LongArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        sampleCount = `in`.readBytes(4).toInt()

        //optional fields
        isDataOffsetPresent = flags and 1 == 1
        if (isDataOffsetPresent) dataOffset = `in`.readBytes(4)
        isFirstSampleFlagsPresent = flags and 4 == 4
        if (isFirstSampleFlagsPresent) firstSampleFlags = `in`.readBytes(4)

        //all fields are optional
        isSampleDurationPresent = flags and 0x100 == 0x100
        if (isSampleDurationPresent) sampleDuration = LongArray(sampleCount)
        isSampleSizePresent = flags and 0x200 == 0x200
        if (isSampleSizePresent) sampleSize = LongArray(sampleCount)
        isSampleFlagsPresent = flags and 0x400 == 0x400
        if (isSampleFlagsPresent) sampleFlags = LongArray(sampleCount)
        isSampleCompositionTimeOffsetPresent = flags and 0x800 == 0x800
        if (isSampleCompositionTimeOffsetPresent) sampleCompositionTimeOffset = LongArray(sampleCount)
        var i = 0
        while (i < sampleCount && getLeft(`in`) > 0) {
            if (isSampleDurationPresent) sampleDuration[i] = `in`.readBytes(4)
            if (isSampleSizePresent) sampleSize[i] = `in`.readBytes(4)
            if (isSampleFlagsPresent) sampleFlags[i] = `in`.readBytes(4)
            if (isSampleCompositionTimeOffsetPresent) sampleCompositionTimeOffset[i] = `in`.readBytes(4)
            i++
        }
    }
}
