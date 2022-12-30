package net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class AudioSampleGroupEntry :
    net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries.SampleGroupDescriptionEntry("Audio Sample Group Entry") {
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream?) {
    }
}
