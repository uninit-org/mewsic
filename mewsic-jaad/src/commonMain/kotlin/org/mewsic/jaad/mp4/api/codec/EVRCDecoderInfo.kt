package org.mewsic.jaad.mp4.api.codec

import org.mewsic.jaad.mp4.api.DecoderInfo
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox

org.mewsic.jaad.mp4.boxes.impl .sampleentries.codec.EVRCSpecificBox
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox
import org.mewsic.jaad.mp4.api.DecoderInfo

class EVRCDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: EVRCSpecificBox

    init {
        this.box = box as EVRCSpecificBox
    }

    val decoderVersion: Int
        get() = box.decoderVersion
    val vendor: Long
        get() = box.vendor
    val framesPerSample: Int
        get() = box.framesPerSample
}
