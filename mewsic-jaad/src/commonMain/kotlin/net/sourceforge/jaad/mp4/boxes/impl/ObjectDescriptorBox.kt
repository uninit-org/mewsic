package net.sourceforge.jaad.mp4.boxes.impl
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.od.Descriptor

class ObjectDescriptorBox : FullBox("Object Descriptor Box") {
    private var objectDescriptor: Descriptor? = null
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        objectDescriptor = Descriptor.createDescriptor(`in`)
    }

    fun getObjectDescriptor(): Descriptor? {
        return objectDescriptor
    }
}
