package org.mewsic.jaad.mp4.api.codec

import org.mewsic.jaad.mp4.api.DecoderInfo
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox
import org.mewsic.jaad.mp4.boxes.impl.sampleentries.codec.H263SpecificBox


class H263DecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: H263SpecificBox

    init {
        this.box = box as H263SpecificBox
    }

    val decoderVersion: Int
        get() = box.decoderVersion
    val vendor: Long
        get() = box.vendor
    val level: Int
        get() = box.level
    val profile: Int
        get() = box.profile
}
