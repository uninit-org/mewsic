package org.mewsic.jaad.mp4.api.codec

import org.mewsic.jaad.mp4.api.DecoderInfo
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.SMVSpecificBox


class SMVDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: SMVSpecificBox

    init {
        this.box = box as SMVSpecificBox
    }

    val decoderVersion: Int
        get() = box.decoderVersion
    val vendor: Long
        get() = box.vendor
    val framesPerSample: Int
        get() = box.framesPerSample
}
