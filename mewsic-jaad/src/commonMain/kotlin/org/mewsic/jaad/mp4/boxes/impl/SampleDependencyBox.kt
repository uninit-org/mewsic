package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import net.sourceforge.jaad.mp4.boxes.FullBox

/**
 * This box contains the sample dependencies for each switching sample. The
 * dependencies are stored in the table, one record for each sample. The size of
 * the table is taken from the the Sample Size Box ('stsz') or Compact Sample
 * Size Box ('stz2').
 *
 * @author in-somnia
 */
class SampleDependencyBox : FullBox("Sample Dependency Box") {
    /**
     * The dependency count is an integer that counts the number of samples
     * in the source track on which this switching sample directly depends.
     *
     * @return all dependency counts
     */
    lateinit var dependencyCount: IntArray

    /**
     * The relative sample number is an integer that identifies a sample in
     * the source track. The relative sample numbers are encoded as follows.
     * If there is a sample in the source track with the same decoding time,
     * it has a relative sample number of 0. Whether or not this sample
     * exists, the sample in the source track which immediately precedes the
     * decoding time of the switching sample has relative sample number –1,
     * the sample before that –2, and so on. Similarly, the sample in the
     * source track which immediately follows the decoding time of the
     * switching sample has relative sample number +1, the sample after that
     * +2, and so on.
     *
     * @return all relative sample numbers
     */
    lateinit var relativeSampleNumber: Array<IntArray>

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val sampleCount: Int =
            (parent!!.getChild(BoxTypes.SAMPLE_SIZE_BOX) as SampleSizeBox).getSampleCount()
        var j: Int
        for (i in 0 until sampleCount) {
            dependencyCount[i] = `in`.readBytes(2).toInt()
            j = 0
            while (j < dependencyCount[i]) {
                relativeSampleNumber[i][j] = `in`.readBytes(2).toInt()
                j++
            }
        }
    }
}
