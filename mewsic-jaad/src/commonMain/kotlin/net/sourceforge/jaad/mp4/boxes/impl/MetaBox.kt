package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

//needs to be defined, because readChildren() is not called by factory
/* TODO: this class shouldn't be needed. at least here, things become too
complicated. change this!!! */
class MetaBox : FullBox("Meta Box") {
    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        // some encoders (such as Android's MexiaMuxer) do not include
        // the version and flags fields in the meta box, instead going
        // directly to the hdlr box
        val possibleType: Long = `in`.peekBytes(8) and 0xFFFFFFFFL
        if (possibleType != BoxTypes.HANDLER_BOX) {
            super.decode(`in`)
        }
        readChildren(`in`)
    }
}
