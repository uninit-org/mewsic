/*
 *  Copyright (C) 2011 in-somnia
 * 
 *  This file is part of JAAD.
 * 
 *  JAAD is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation; either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  JAAD is distributed in the hope that it will be useful, but WITHOUT 
 
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General 
 *  Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.jaad.mp4.boxes.impl.sampleentries

import net.sourceforge.jaad.mp4.MP4InputStream

class FDHintSampleEntry : net.sourceforge.jaad.mp4.boxes.impl.sampleentries.SampleEntry("FD Hint Sample Entry") {
    private var hintTrackVersion = 0
    private var highestCompatibleVersion = 0

    /**
     * The partition entry ID indicates the partition entry in the FD item
     * information box. A zero value indicates that no partition entry is
     * associated with this sample entry, e.g., for FDT. If the corresponding FD
     * hint track contains only overhead data this value should indicate the
     * partition entry whose overhead data is in question.
     *
     * @return the partition entry ID
     */
    var partitionEntryID = 0
        private set

    /**
     * The FEC overhead is a floating point value indicating the percentage
     * protection overhead used by the hint sample(s). The intention of
     * providing this value is to provide characteristics to help a server
     * select a session group (and corresponding FD hint tracks). If the
     * corresponding FD hint track contains only overhead data this value should
     * indicate the protection overhead achieved by using all FD hint tracks in
     * a session group up to the FD hint track in question.
     *
     * @return the FEC overhead
     */
    var fECOverhead = 0.0
        private set

    @Throws(java.io.IOException::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        hintTrackVersion = `in`.readBytes(2) as Int
        highestCompatibleVersion = `in`.readBytes(2) as Int
        partitionEntryID = `in`.readBytes(2) as Int
        fECOverhead = `in`.readFixedPoint(8, 8)
        readChildren(`in`)
    }
}
