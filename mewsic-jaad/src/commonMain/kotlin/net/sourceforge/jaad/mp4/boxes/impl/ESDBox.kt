package net.sourceforge.jaad.mp4.boxes.impl
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.od.ESDescriptor
import net.sourceforge.jaad.mp4.od.ObjectDescriptor

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
