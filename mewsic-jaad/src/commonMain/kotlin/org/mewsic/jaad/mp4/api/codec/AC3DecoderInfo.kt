package org.mewsic.jaad.mp4.api.codec

import org.mewsic.jaad.mp4.api.DecoderInfo
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.AC3SpecificBox
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox

class AC3DecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: AC3SpecificBox

    init {
        this.box = box as AC3SpecificBox
    }

    val isLfeon: Boolean
        get() = box.isLfeon
    val fscod: Int
        get() = box.fscod
    val bsmod: Int
        get() = box.bsmod
    val bsid: Int
        get() = box.bsid
    val bitRateCode: Int
        get() = box.bitRateCode
    val acmod: Int
        get() = box.acmod
}
