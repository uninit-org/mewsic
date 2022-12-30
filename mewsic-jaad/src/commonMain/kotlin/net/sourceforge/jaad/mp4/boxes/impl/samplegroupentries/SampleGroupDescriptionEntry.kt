package net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

abstract class SampleGroupDescriptionEntry protected constructor(name: String) : BoxImpl(name) {
    @Throws(Exception::class)
    abstract override fun decode(`in`: MP4InputStream)
}
