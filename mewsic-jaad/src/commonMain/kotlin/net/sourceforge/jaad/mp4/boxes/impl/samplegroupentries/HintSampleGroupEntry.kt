package net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries

import net.sourceforge.jaad.mp4.MP4InputStream

class HintSampleGroupEntry :
    net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries.SampleGroupDescriptionEntry("Hint Sample Group Entry") {
    @Throws(java.io.IOException::class)
    override fun decode(`in`: MP4InputStream?) {
    }
}
