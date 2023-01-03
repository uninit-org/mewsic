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

class TextMetadataSampleEntry :
    MetadataSampleEntry("Text Metadata Sample Entry") {
    /**
     * Provides a MIME type which identifies the content format of the timed
     * metadata. Examples for this field are 'text/html' and 'text/plain'.
     *
     * @return the content's MIME type
     */
    var mimeType: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        mimeType = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
    }
}