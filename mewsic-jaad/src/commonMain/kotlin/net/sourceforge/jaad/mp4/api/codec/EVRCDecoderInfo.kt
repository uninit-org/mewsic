package net.sourceforge.jaad.mp4.api.codec
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.EVRCSpecificBox
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox
import net.sourceforge.jaad.mp4.api.DecoderInfo

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
