package net.sourceforge.jaad.mp4.api.codec

import net.sourceforge.jaad.mp4.api.DecoderInfo

class AC3DecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: AC3SpecificBox

    init {
        this.box = box as AC3SpecificBox
    }

    val isLfeon: Boolean
        get() = box.isLfeon()
    val fscod: Int
        get() = box.getFscod()
    val bsmod: Int
        get() = box.getBsmod()
    val bsid: Int
        get() = box.getBsid()
    val bitRateCode: Int
        get() = box.getBitRateCode()
    val acmod: Int
        get() = box.getAcmod()
}
