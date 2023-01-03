package org.mewsic.jaad.mp4.boxes.impl.fd

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

/**
 * The FD session group box is optional, although it is mandatory for files
 * containing more than one FD hint track. It contains a list of sessions as
 * well as all file groups and hint tracks that belong to each session. An FD
 * session sends simultaneously over all FD hint tracks (channels) that are
 * listed in the FD session group box for a particular FD session.
 *
 * Only one session group should be processed at any time. The first listed
 * hint track in a session group specifies the base channel. If the server has
 * no preference between the session groups, the default choice should be the
 * first session group. The group IDs of all file groups containing the files
 * referenced by the hint tracks shall be included in the list of file groups.
 * The file group IDs can in turn be translated into file group names (using the
 * group ID to name box) that can be included by the server in FDTs.
 *
 * @author in-somnia
 */
class FDSessionGroupBox : FullBox("FD Session Group Box") {
    /**
     * A group ID indicates a file group that the session group complies with.
     *
     * @return all group IDs for all session groups
     */
    lateinit var groupIDs: Array<LongArray?>
        private set

    /**
     * A hint track ID specifies the track ID of the FD hint track belonging to
     * a particular session group. Note that one FD hint track corresponds to
     * one LCT channel.
     *
     * @return all hint track IDs for all session groups
     */
    lateinit var hintTrackIDs: Array<LongArray?>
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val sessionGroups = `in`.readBytes(2).toInt()
        groupIDs = arrayOfNulls(sessionGroups)
        hintTrackIDs = arrayOfNulls(sessionGroups)
        var j: Int
        var entryCount: Int
        var channelsInSessionGroup: Int
        for (i in 0 until sessionGroups) {
            entryCount = `in`.read()
            groupIDs[i] = LongArray(entryCount)
            j = 0
            while (j < entryCount) {
                groupIDs[i]!![j] = `in`.readBytes(4)
                j++
            }
            channelsInSessionGroup = `in`.readBytes(2).toInt()
            hintTrackIDs[i] = LongArray(channelsInSessionGroup)
            j = 0
            while (j < channelsInSessionGroup) {
                hintTrackIDs[i]!![j] = `in`.readBytes(4)
                j++
            }
        }
    }
}
