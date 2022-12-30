package net.sourceforge.jaad.mp4.api.codec
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.QCELPSpecificBox

import net.sourceforge.jaad.mp4.api.DecoderInfo

class QCELPDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: QCELPSpecificBox

    init {
        this.box = box as QCELPSpecificBox
    }

    val decoderVersion: Int
        get() = box.decoderVersion
    val vendor: Long
        get() = box.vendor
    val framesPerSample: Int
        get() = box.framesPerSample
}