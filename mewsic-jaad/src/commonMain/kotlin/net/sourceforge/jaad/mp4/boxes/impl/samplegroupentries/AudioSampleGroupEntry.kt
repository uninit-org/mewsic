package net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries

import net.sourceforge.jaad.mp4.MP4InputStream

class AudioSampleGroupEntry :
    net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries.SampleGroupDescriptionEntry("Audio Sample Group Entry") {
    @Throws(java.io.IOException::class)
    override fun decode(`in`: MP4InputStream?) {
    }
}
