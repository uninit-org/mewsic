package net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class HintSampleGroupEntry :
    net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries.SampleGroupDescriptionEntry("Hint Sample Group Entry") {
    @Throws(Exception::class)
    override override fun decode(`in`: MP4InputStream?) {
    }
}
