package org.mewsic.jaad.mp4.boxes.impl.samplegroupentries

import org.mewsic.jaad.mp4.MP4InputStream

class HintSampleGroupEntry :
    SampleGroupDescriptionEntry("Hint Sample Group Entry") {
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream?) {
    }
}
