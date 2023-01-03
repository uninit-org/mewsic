package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox
import org.mewsic.jaad.mp4.od.ESDescriptor
import org.mewsic.jaad.mp4.od.ObjectDescriptor

/**
 * The entry sample descriptor (ESD) box is a container for entry descriptors.
 * If used, it is located in a sample entry. Instead of an `ESDBox` a
 * `CodecSpecificBox` may be present.
 *
 * @author in-somnia
 */
class ESDBox : FullBox("ESD Box") {
    private var esd: ESDescriptor? = null

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        esd = ObjectDescriptor.createDescriptor(`in`) as ESDescriptor
    }

    val entryDescriptor: ESDescriptor?
        get() = esd
}
