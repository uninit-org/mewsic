package net.sourceforge.jaad.mp4.api.codec
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.api.DecoderInfo
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.AVCSpecificBox
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox

class AVCDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: AVCSpecificBox

    init {
        this.box = box as AVCSpecificBox
    }

    val configurationVersion: Int
        get() = box.configurationVersion
    val profile: Int
        get() = box.profile
    val profileCompatibility: Byte
        get() = box.profileCompatibility
    val level: Int
        get() = box.level
    val lengthSize: Int
        get() = box.lengthSize
    val sequenceParameterSetNALUnits: Array<ByteArray>
        get() = box.sequenceParameterSetNALUnits
    val pictureParameterSetNALUnits: Array<ByteArray>
        get() = box.pictureParameterSetNALUnits
}
