package org.mewsic.jaad.mp4.api.codec

import org.mewsic.jaad.mp4.api.DecoderInfo
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.QCELPSpecificBox

org.mewsic.jaad.mp4.boxes.impl .sampleentries.codec.CodecSpecificBox
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.QCELPSpecificBox

import org.mewsic.jaad.mp4.api.DecoderInfo

class QCELPDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: QCELPSpecificBox

    init {
        this.box = box
    }

    val decoderVersion: Int
        get() = box.decoderVersion
    val vendor: Long
        get() = box.vendor
    val framesPerSample: Int
        get() = box.framesPerSample
}