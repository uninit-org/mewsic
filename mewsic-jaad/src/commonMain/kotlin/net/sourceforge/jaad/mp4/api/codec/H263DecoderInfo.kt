package net.sourceforge.jaad.mp4.api.codec
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStreamimport net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.H263SpecificBox
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox

import net.sourceforge.jaad.mp4.api.DecoderInfo

class H263DecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: H263SpecificBox

    init {
        this.box = box as H263SpecificBox
    }

    val decoderVersion: Int
        get() = box.decoderVersion
    val vendor: Long
        get() = box.vendor
    val level: Int
        get() = box.level
    val profile: Int
        get() = box.profile
}
