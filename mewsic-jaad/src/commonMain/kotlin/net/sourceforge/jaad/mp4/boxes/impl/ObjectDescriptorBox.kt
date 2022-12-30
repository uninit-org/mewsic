package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class ObjectDescriptorBox : FullBox("Object Descriptor Box") {
    private var objectDescriptor: Descriptor? = null
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream?) {
        super.decode(`in`)
        objectDescriptor = Descriptor.createDescriptor(`in`)
    }

    fun getObjectDescriptor(): Descriptor? {
        return objectDescriptor
    }
}
