package net.sourceforge.jaad.mp4.api.codec

import net.sourceforge.jaad.mp4.api.DecoderInfo

class AVCDecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: AVCSpecificBox

    init {
        this.box = box as AVCSpecificBox
    }

    val configurationVersion: Int
        get() = box.getConfigurationVersion()
    val profile: Int
        get() = box.getProfile()
    val profileCompatibility: Byte
        get() = box.getProfileCompatibility()
    val level: Int
        get() = box.getLevel()
    val lengthSize: Int
        get() = box.getLengthSize()
    val sequenceParameterSetNALUnits: Array<ByteArray>
        get() = box.getSequenceParameterSetNALUnits()
    val pictureParameterSetNALUnits: Array<ByteArray>
        get() = box.getPictureParameterSetNALUnits()
}
