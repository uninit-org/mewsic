package net.sourceforge.jaad.mp4.boxes

import net.sourceforge.jaad.mp4.MP4InputStream

open class FullBox(name: String) : BoxImpl(name) {
    protected var version = 0
    protected var flags = 0
    @Throws(Exception::class)
    open fun decode(`in`: MP4InputStream) {
        version = `in`.read()
        flags = `in`.readBytes(3) as Int
    }
}
