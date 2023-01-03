package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox
import org.mewsic.jaad.mp4.od.Descriptor

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
