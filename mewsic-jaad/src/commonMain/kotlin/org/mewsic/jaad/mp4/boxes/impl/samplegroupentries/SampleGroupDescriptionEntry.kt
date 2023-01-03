package net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.BoxImpl

abstract class SampleGroupDescriptionEntry protected constructor(name: String) : BoxImpl(name) {
    @Throws(Exception::class)
    abstract override fun decode(`in`: MP4InputStream)
}
