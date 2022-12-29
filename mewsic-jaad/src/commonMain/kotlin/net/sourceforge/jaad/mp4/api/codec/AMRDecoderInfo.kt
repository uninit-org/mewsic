package net.sourceforge.jaad.mp4.api.codec

import net.sourceforge.jaad.mp4.api.DecoderInfo

class AMRDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: AMRSpecificBox

    init {
        this.box = box as AMRSpecificBox
    }

    val decoderVersion: Int
        get() = box.getDecoderVersion()
    val vendor: Long
        get() = box.getVendor()
    val modeSet: Int
        get() = box.getModeSet()
    val modeChangePeriod: Int
        get() = box.getModeChangePeriod()
    val framesPerSample: Int
        get() = box.getFramesPerSample()
}
