package net.sourceforge.jaad.mp4.api.codec
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStreamimport net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.SMVSpecificBox
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox

import net.sourceforge.jaad.mp4.api.DecoderInfo

class SMVDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: SMVSpecificBox

    init {
        this.box = box as SMVSpecificBox
    }

    val decoderVersion: Int
        get() = box.decoderVersion
    val vendor: Long
        get() = box.vendor
    val framesPerSample: Int
        get() = box.framesPerSample
}
