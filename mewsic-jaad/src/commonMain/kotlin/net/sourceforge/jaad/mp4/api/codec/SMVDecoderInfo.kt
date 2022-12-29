package net.sourceforge.jaad.mp4.api.codec

import net.sourceforge.jaad.mp4.api.DecoderInfo

class SMVDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: SMVSpecificBox

    init {
        this.box = box as SMVSpecificBox
    }

    val decoderVersion: Int
        get() = box.getDecoderVersion()
    val vendor: Long
        get() = box.getVendor()
    val framesPerSample: Int
        get() = box.getFramesPerSample()
}
