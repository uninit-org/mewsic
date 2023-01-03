package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

/**
 * This box sets up default values used by the movie fragments. By setting
 * defaults in this way, space and complexity can be saved in each Track
 * Fragment Box.
 *
 * @author in-somnia
 */
class TrackExtendsBox : FullBox("Track Extends Box") {
    /**
     * The track ID identifies the track; this shall be the track ID of a track
     * in the Movie Box.
     *
     * @return the track ID
     */
    var trackID: Long = 0
        private set

    /**
     * The default sample description index used in the track fragments.
     *
     * @return the default sample description index
     */
    var defaultSampleDescriptionIndex: Long = 0
        private set

    /**
     * The default sample duration used in the track fragments.
     *
     * @return the default sample duration
     */
    var defaultSampleDuration: Long = 0
        private set

    /**
     * The default sample size used in the track fragments.
     *
     * @return the default sample size
     */
    var defaultSampleSize: Long = 0
        private set
    private var defaultSampleFlags: Long = 0

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        trackID = `in`.readBytes(4)
        defaultSampleDescriptionIndex = `in`.readBytes(4)
        defaultSampleDuration = `in`.readBytes(4)
        defaultSampleSize = `in`.readBytes(4)
        /* 6 bits reserved
		 * 2 bits sampleDependsOn
		 * 2 bits sampleIsDependedOn
		 * 2 bits sampleHasRedundancy
		 * 3 bits samplePaddingValue
		 * 1 bit sampleIsDifferenceSample
		 * 16 bits sampleDegradationPriority
		 */defaultSampleFlags = `in`.readBytes(4)
    }

    val sampleDependsOn: Int
        /**
         * The default 'sample depends on' value as defined in the
         * SampleDependencyTypeBox.
         *
         * @see SampleDependencyTypeBox.getSampleDependsOn
         * @return the default 'sample depends on' value
         */
        get() = (defaultSampleFlags shr 24 and 3L).toInt()
    val sampleIsDependedOn: Int
        /**
         * The default 'sample is depended on' value as defined in the
         * SampleDependencyTypeBox.
         *
         * @see SampleDependencyTypeBox.getSampleIsDependedOn
         * @return the default 'sample is depended on' value
         */
        get() = (defaultSampleFlags shr 22 and 3L).toInt()
    val sampleHasRedundancy: Int
        /**
         * The default 'sample has redundancy' value as defined in the
         * SampleDependencyBox.
         *
         * @see SampleDependencyTypeBox.getSampleHasRedundancy
         * @return the default 'sample has redundancy' value
         */
        get() = (defaultSampleFlags shr 20 and 3L).toInt()
    val samplePaddingValue: Int
        /**
         * The default padding value as defined in the PaddingBitBox.
         *
         * @see PaddingBitBox.getPad1
         * @return the default padding value
         */
        get() = (defaultSampleFlags shr 17 and 7L).toInt()
    val isSampleDifferenceSample: Boolean
        get() = defaultSampleFlags shr 16 and 1L == 1L
    val sampleDegradationPriority: Int
        /**
         * The default degradation priority for the samples.
         * @return the default degradation priority
         */
        get() = (defaultSampleFlags and 0xFFFFL).toInt()
}
