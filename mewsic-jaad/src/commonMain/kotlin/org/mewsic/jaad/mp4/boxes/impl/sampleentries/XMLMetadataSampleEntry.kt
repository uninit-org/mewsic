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

class XMLMetadataSampleEntry :
    MetadataSampleEntry("XML Metadata Sample Entry") {
    /**
     * Gives the namespace of the schema for the timed XML metadata. This is
     * needed for identifying the type of metadata, e.g. gBSD or AQoS
     * (MPEG-21-7) and for decoding using XML aware encoding mechanisms such as
     * BiM.
     * @return the namespace
     */
    var namespace: String? = null
        private set

    /**
     * Optionally provides an URL to find the schema corresponding to the
     * namespace. This is needed for decoding of the timed metadata by XML aware
     * encoding mechanisms such as BiM.
     * @return the schema's URL
     */
    var schemaLocation: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        namespace = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
        schemaLocation = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
    }
}
