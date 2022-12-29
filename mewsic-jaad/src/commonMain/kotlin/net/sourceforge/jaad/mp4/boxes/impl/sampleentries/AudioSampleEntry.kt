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

class AudioSampleEntry(name: String?) : net.sourceforge.jaad.mp4.boxes.impl.sampleentries.SampleEntry(name) {
    var channelCount = 0
        private set
    var sampleSize = 0
        private set
    var sampleRate = 0
        private set

    @Throws(java.io.IOException::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        `in`.skipBytes(8) //reserved
        channelCount = `in`.readBytes(2) as Int
        sampleSize = `in`.readBytes(2) as Int
        `in`.skipBytes(2) //pre-defined: 0
        `in`.skipBytes(2) //reserved
        sampleRate = `in`.readBytes(2) as Int
        `in`.skipBytes(2) //not used by samplerate
        readChildren(`in`)
    }
}
