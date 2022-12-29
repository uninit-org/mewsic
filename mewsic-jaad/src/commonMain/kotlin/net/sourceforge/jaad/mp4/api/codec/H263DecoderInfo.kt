package net.sourceforge.jaad.mp4.api.codec

import net.sourceforge.jaad.mp4.api.DecoderInfo

class H263DecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: H263SpecificBox

    init {
        this.box = box as H263SpecificBox
    }

    val decoderVersion: Int
        get() = box.getDecoderVersion()
    val vendor: Long
        get() = box.getVendor()
    val level: Int
        get() = box.getLevel()
    val profile: Int
        get() = box.getProfile()
}
