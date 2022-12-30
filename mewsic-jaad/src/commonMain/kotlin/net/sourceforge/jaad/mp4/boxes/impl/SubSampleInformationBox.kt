package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The Sub-Sample Information box is designed to contain sub-sample information.
 * A sub-sample is a contiguous range of bytes of a sample. The specific
 * definition of a sub-sample shall be supplied for a given coding system (e.g.
 * for ISO/IEC 14496-10, Advanced Video Coding). In the absence of such a
 * specific definition, this box shall not be applied to samples using that
 * coding system.
 * The table is sparsely coded; the table identifies which samples have
 * sub-sample structure by recording the difference in sample-number between
 * each entry. The first entry in the table records the sample number of the
 * first sample having sub-sample information.
 *
 * @author in-somnia
 */
class SubSampleInformationBox : FullBox("Sub Sample Information Box") {
    /**
     * The sample delta for each entry is an integer that specifies the sample
     * number of the sample having sub-sample structure. It is coded as the
     * difference between the desired sample number, and the sample number
     * indicated in the previous entry. If the current entry is the first entry,
     * the value indicates the sample number of the first sample having
     * sub-sample information, that is, the value is the difference between the
     * sample number and zero.
     *
     * @return the sample deltas for all entries
     */
    var sampleDelta: LongArray
        private set

    /**
     * The subsample size is an integer that specifies the size, in bytes, of a
     * specific sub-sample in a specific entry.
     *
     * @return the sizes of all subsamples
     */
    var subsampleSize: Array<LongArray?>
        private set

    /**
     * The subsample priority is an integer specifying the degradation priority
     * for a specific sub-sample in a specific entry. Higher values indicate
     * sub-samples which are important to, and have a greater impact on, the
     * decoded quality.
     *
     * @return all subsample priorities
     */
    var subsamplePriority: Array<IntArray?>
        private set

    /**
     * If true, the sub-sample is required to decode the current sample, while
     * false means the sub-sample is not required to decode the current sample
     * but may be used for enhancements, e.g., the sub-sample consists of
     * supplemental enhancement information (SEI) messages.
     *
     * @return a list of flags indicating if a specific subsample is discardable
     */
    var discardable: Array<BooleanArray?>
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val len = if (version === 1) 4 else 2
        val entryCount = `in`.readBytes(4) as Int
        sampleDelta = LongArray(entryCount)
        subsampleSize = arrayOfNulls(entryCount)
        subsamplePriority = arrayOfNulls(entryCount)
        discardable = arrayOfNulls(entryCount)
        var j: Int
        var subsampleCount: Int
        for (i in 0 until entryCount) {
            sampleDelta[i] = `in`.readBytes(4)
            subsampleCount = `in`.readBytes(2) as Int
            subsampleSize[i] = LongArray(subsampleCount)
            subsamplePriority[i] = IntArray(subsampleCount)
            discardable[i] = BooleanArray(subsampleCount)
            j = 0
            while (j < subsampleCount) {
                subsampleSize[i]!![j] = `in`.readBytes(len)
                subsamplePriority[i]!![j] = `in`.read()
                discardable[i]!![j] = `in`.read() and 1 === 1
                `in`.skipBytes(4) //reserved
                j++
            }
        }
    }
}
