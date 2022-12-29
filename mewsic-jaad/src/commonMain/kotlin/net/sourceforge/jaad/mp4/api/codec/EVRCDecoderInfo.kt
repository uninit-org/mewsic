package net.sourceforge.jaad.mp4.api.codec

import net.sourceforge.jaad.mp4.api.DecoderInfo

class EVRCDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: EVRCSpecificBox

    init {
        this.box = box as EVRCSpecificBox
    }

    val decoderVersion: Int
        get() = box.getDecoderVersion()
    val vendor: Long
        get() = box.getVendor()
    val framesPerSample: Int
        get() = box.getFramesPerSample()
}
