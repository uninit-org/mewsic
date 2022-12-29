package net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries

import net.sourceforge.jaad.mp4.MP4InputStream

abstract class SampleGroupDescriptionEntry protected constructor(name: String?) : BoxImpl(name) {
    @Throws(java.io.IOException::class)
    abstract fun decode(`in`: MP4InputStream?)
}
