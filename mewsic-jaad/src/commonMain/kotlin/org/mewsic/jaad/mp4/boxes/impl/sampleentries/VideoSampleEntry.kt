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
package org.mewsic.jaad.mp4.boxes.impl.sampleentries

import org.mewsic.jaad.mp4.MP4InputStream

class VideoSampleEntry(name: String) : SampleEntry(name) {
    /**
     * The width is the maximum visual width of the stream described by this
     * sample description, in pixels.
     */
    var width = 0
        private set

    /**
     * The height is the maximum visual height of the stream described by this
     * sample description, in pixels.
     */
    var height = 0
        private set

    /**
     * The horizontal resolution of the image in pixels-per-inch, as a floating
     * point value.
     */
    var horizontalResolution = 0.0
        private set

    /**
     * The vertical resolution of the image in pixels-per-inch, as a floating
     * point value.
     */
    var verticalResolution = 0.0
        private set

    /**
     * The frame count indicates how many frames of compressed video are stored
     * in each sample.
     */
    var frameCount = 0
        private set

    /**
     * The depth takes one of the following values
     * DEFAULT_DEPTH (0x18) – images are in colour with no alpha
     */
    var depth = 0
        private set

    /**
     * The compressor name, for informative purposes.
     */
    var compressorName: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        `in`.skipBytes(2) //pre-defined: 0
        `in`.skipBytes(2) //reserved
        //3x32 pre_defined
        `in`.skipBytes(4) //pre-defined: 0
        `in`.skipBytes(4) //pre-defined: 0
        `in`.skipBytes(4) //pre-defined: 0
        width = `in`.readBytes(2).toInt()
        height = `in`.readBytes(2).toInt()
        horizontalResolution = `in`.readFixedPoint(16, 16)
        verticalResolution = `in`.readFixedPoint(16, 16)
        `in`.skipBytes(4) //reserved
        frameCount = `in`.readBytes(2).toInt()
        val len: Int = `in`.read()
        compressorName = `in`.readString(len)
        `in`.skipBytes((31 - len).toLong())
        depth = `in`.readBytes(2).toInt()
        `in`.skipBytes(2) //pre-defined: -1
        readChildren(`in`)
    }
}
