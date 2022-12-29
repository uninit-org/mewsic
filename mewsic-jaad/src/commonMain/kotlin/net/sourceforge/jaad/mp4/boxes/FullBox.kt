package net.sourceforge.jaad.mp4.boxes

import net.sourceforge.jaad.mp4.MP4InputStream

class FullBox(name: String) : net.sourceforge.jaad.mp4.boxes.BoxImpl(name) {
    protected var version = 0
    protected var flags = 0
    @Throws(java.io.IOException::class)
    override fun decode(`in`: MP4InputStream) {
        version = `in`.read()
        flags = `in`.readBytes(3) as Int
    }
}
