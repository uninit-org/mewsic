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
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class RTPHintSampleEntry : net.sourceforge.jaad.mp4.boxes.impl.sampleentries.SampleEntry("RTP Hint Sample Entry") {
    private var hintTrackVersion = 0
    private var highestCompatibleVersion = 0

    /**
     * The maximum packet size indicates the size of the largest packet that
     * this track will generate.
     *
     * @return the maximum packet size
     */
    var maxPacketSize: Long = 0
        private set

    @Throws(Exception::class)
    override override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        hintTrackVersion = `in`.readBytes(2) as Int
        highestCompatibleVersion = `in`.readBytes(2) as Int
        maxPacketSize = `in`.readBytes(4)
    }
}
