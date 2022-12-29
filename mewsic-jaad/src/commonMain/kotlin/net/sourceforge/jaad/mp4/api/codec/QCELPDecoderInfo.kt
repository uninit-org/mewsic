package net.sourceforge.jaad.mp4.api.codec

import net.sourceforge.jaad.mp4.api.DecoderInfo

class QCELPDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: QCELPSpecificBox

    init {
        this.box = box as QCELPSpecificBox
    }

    val decoderVersion: Int
        get() = box.getDecoderVersion()
    val vendor: Long
        get() = box.getVendor()
    val framesPerSample: Int
        get() = box.getFramesPerSample()
}
