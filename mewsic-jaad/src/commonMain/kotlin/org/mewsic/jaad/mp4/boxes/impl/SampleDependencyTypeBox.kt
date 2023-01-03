package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import net.sourceforge.jaad.mp4.boxes.FullBox

/**
 * This optional table answers three questions about sample dependency:
 *
 *  * does this sample depend on others (is it an I-picture)?
 *  * do no other samples depend on this one?
 *  * does this sample contain multiple (redundant) encodings of the data at
 * this time-instant (possibly with different dependencies)?
 *
 *
 * In the absence of this table:
 *
 *  * the sync sample table answers the first question; in most video codecs,
 * I-pictures are also sync points
 *  * the dependency of other samples on this one is unknown
 *  * the existence of redundant coding is unknown
 *
 *
 * When performing 'trick' modes, such as fast-forward, it is possible to use
 * the first piece of information to locate independently decodable samples.
 * Similarly, when performing random access, it may be necessary to locate the
 * previous sync point or random access recovery point, and roll-forward from
 * the sync point or the pre-roll starting point of the random access recovery
 * point to the desired point. While rolling forward, samples on which no others
 * depend need not be retrieved or decoded.
 * The value of 'sample is depended on' is independent of the existence of
 * redundant codings. However, a redundant coding may have different
 * dependencies from the primary coding; if redundant codings are available, the
 * value of 'sample depends on' documents only the primary coding.
 *
 * A Sample Dependency Box may also occur in the Track Fragment Box.
 *
 * @author in-somnia
 */
class SampleDependencyTypeBox : FullBox("Sample Dependency Type Box") {
    /**
     * The 'sample depends on' field takes one of the following four values:
     * 0: the dependency of this sample is unknown
     * 1: this sample does depend on others (not an I picture)
     * 2: this sample does not depend on others (I picture)
     * 3: reserved
     *
     * @return a list of 'sample depends on' values for all samples
     */
    lateinit var sampleDependsOn: IntArray
        private set

    /**
     * The 'sample is depended on' field takes one of the following four values:
     * 0: the dependency of other samples on this sample is unknown
     * 1: other samples may depend on this one (not disposable)
     * 2: no other sample depends on this one (disposable)
     * 3: reserved
     *
     * @return a list of 'sample is depended on' values for all samples
     */
    lateinit var sampleIsDependedOn: IntArray
        private set

    /**
     * The 'sample has redundancy' field takes one of the following four values:
     * 0: it is unknown whether there is redundant coding in this sample
     * 1: there is redundant coding in this sample
     * 2: there is no redundant coding in this sample
     * 3: reserved
     *
     * @return a list of 'sample has redundancy' values for all samples
     */
    lateinit var sampleHasRedundancy: IntArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)

        //get number of samples from SampleSizeBox
        var sampleCount: Long = -1
        if (parent!!.hasChild(BoxTypes.SAMPLE_SIZE_BOX)) sampleCount =
            (parent!!.getChild(BoxTypes.SAMPLE_SIZE_BOX) as SampleSizeBox).getSampleCount()
                .toLong()
        //TODO: uncomment when CompactSampleSizeBox is implemented
        //else if(parent.containsChild(BoxTypes.COMPACT_SAMPLE_SIZE_BOX)) sampleCount = ((CompactSampleSizeBox)parent.getChild(BoxTypes.SAMPLE_SIZE_BOX)).getSampleSize();
        sampleHasRedundancy = IntArray(sampleCount.toInt())
        sampleIsDependedOn = IntArray(sampleCount.toInt())
        sampleDependsOn = IntArray(sampleCount.toInt())
        var b: Byte
        for (i in 0 until sampleCount) {
            b = `in`.read() as Byte
            /* 2 bits reserved
			 * 2 bits sampleDependsOn
			 * 2 bits sampleIsDependedOn
			 * 2 bits sampleHasRedundancy
			 */sampleHasRedundancy[i.toInt()] = b.toInt() and 3
            sampleIsDependedOn[i.toInt()] = b.toInt() shr 2 and 3
            sampleDependsOn[i.toInt()] = b.toInt() shr 4 and 3
        }
    }
}
