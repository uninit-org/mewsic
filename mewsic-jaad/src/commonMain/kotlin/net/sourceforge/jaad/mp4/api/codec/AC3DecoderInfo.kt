package net.sourceforge.jaad.mp4.api.codec
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.api.DecoderInfo
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.AC3SpecificBox
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox

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
