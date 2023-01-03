package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import net.sourceforge.jaad.mp4.boxes.FullBox

/**
 * This box contains the degradation priority of each sample. The values are
 * stored in the table, one for each sample. Specifications derived from this
 * define the exact meaning and acceptable range of the priority field.
 *
 * @author in-somnia
 */
class DegradationPriorityBox : FullBox("Degradation Priority Box") {
    /**
     * The priority is integer specifying the degradation priority for each
     * sample.
     * @return the list of priorities
     */
    lateinit var priorities: IntArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)

        //get number of samples from SampleSizeBox
        val sampleCount: Int =
            (parent!!.getChild(BoxTypes.SAMPLE_SIZE_BOX) as SampleSizeBox).getSampleCount()
        priorities = IntArray(sampleCount)
        for (i in 0 until sampleCount) {
            priorities[i] = `in`.readBytes(2).toInt()
        }
    }
}
