package org.mewsic.jaad.mp4.api.codec

import org.mewsic.jaad.mp4.api.DecoderInfo
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.AMRSpecificBox
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox

class AMRDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: AMRSpecificBox

    init {
        this.box = box as AMRSpecificBox
    }

    val decoderVersion: Int
        get() = box.decoderVersion
    val vendor: Long
        get() = box.vendor
    val modeSet: Int
        get() = box.modeSet
    val modeChangePeriod: Int
        get() = box.modeChangePeriod
    val framesPerSample: Int
        get() = box.framesPerSample
}
