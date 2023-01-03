package org.mewsic.jaad.mp4.od

import org.mewsic.jaad.mp4.MP4InputStream

/**
 * This class is used if any unknown Descriptor is found in a stream. All
 * contents of the Descriptor will be skipped.
 *
 * @author in-somnia
 */
class UnknownDescriptor : Descriptor() {
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        //content will be skipped
    }
}
