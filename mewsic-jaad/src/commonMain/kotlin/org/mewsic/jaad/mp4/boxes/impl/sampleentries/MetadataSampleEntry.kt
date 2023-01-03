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

abstract class MetadataSampleEntry(name: String) :
    SampleEntry(name) {
    /**
     * A string providing a MIME type which identifies the content encoding of
     * the timed metadata. If not present (an empty string is supplied) the
     * timed metadata is not encoded.
     * An example for this field is 'application/zip'.
     * @return the encoding's MIME-type
     */
    var contentEncoding: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        contentEncoding = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
    }
}
