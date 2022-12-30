package net.sourceforge.jaad.mp4.boxes.impl
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The shadow sync table provides an optional set of sync samples that can be
 * used when seeking or for similar purposes. In normal forward play they are
 * ignored.
 *
 * Each entry in the ShadowSyncTable consists of a pair of sample numbers. The
 * first entry (shadowed-sample-number) indicates the number of the sample that
 * a shadow sync will be defined for. This should always be a non-sync sample
 * (e.g. a frame difference). The second sample number (sync-sample-number)
 * indicates the sample number of the sync sample (i.e. key frame) that can be
 * used when there is a random access at, or before, the shadowed-sample-number.
 *
 * The entries in the ShadowSyncBox shall be sorted based on the
 * shadowed-sample-number field. The shadow sync samples are normally placed in
 * an area of the track that is not presented during normal play (edited out by
 * means of an edit list), though this is not a requirement. The shadow sync
 * table can be ignored and the track will play (and seek) correctly if it is
 * ignored (though perhaps not optimally).
 *
 * The ShadowSyncSample replaces, not augments, the sample that it shadows (i.e.
 * the next sample sent is shadowed-sample-number+1). The shadow sync sample is
 * treated as if it occurred at the time of the sample it shadows, having the
 * duration of the sample it shadows.
 *
 * Hinting and transmission might become more complex if a shadow sample is used
 * also as part of normal playback, or is used more than once as a shadow. In
 * this case the hint track might need separate shadow syncs, all of which can
 * get their media data from the one shadow sync in the media track, to allow
 * for the different time-stamps etc. needed in their headers.
 *
 * @author in-somnia
 */
class ShadowSyncSampleBox : FullBox("Shadow Sync Sample Box") {
    /**
     * A map of sample number pairs:
     * 0 (shadowed-sample-number): gives the number of a sample for which there
     * is an alternative sync sample.
     * 1 (sync-sample-number): gives the number of the alternative sync sample.
     *
     * @return the sample number pairs
     */
    var sampleNumbers: Array<LongArray>
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4).toInt()
        sampleNumbers = Array(entryCount) { LongArray(2) }
        for (i in 0 until entryCount) {
            sampleNumbers[i][0] = `in`.readBytes(4) //shadowedSampleNumber;
            sampleNumbers[i][1] = `in`.readBytes(4) //syncSampleNumber;
        }
    }
}
