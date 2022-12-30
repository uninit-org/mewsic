package net.sourceforge.jaad.mp4.api

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.Box
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import net.sourceforge.jaad.mp4.boxes.impl.ESDBox
import net.sourceforge.jaad.mp4.boxes.impl.SampleDescriptionBox
import net.sourceforge.jaad.mp4.boxes.impl.SoundMediaHeaderBox
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox

class AudioTrack(trak: Box, `in`: MP4InputStream) : Track(trak, `in`) {
    enum class AudioCodec : Codec {
        AAC, AC3, AMR, AMR_WIDE_BAND, EVRC, EXTENDED_AC3, QCELP, SMV, UNKNOWN_AUDIO_CODEC;

        companion object {
            fun forType(type: Long): Codec {
                val ac: Codec
                ac =
                    if (type == BoxTypes.MP4A_SAMPLE_ENTRY) AAC else if (type == BoxTypes.AC3_SAMPLE_ENTRY) AC3 else if (type == BoxTypes.AMR_SAMPLE_ENTRY) AMR else if (type == BoxTypes.AMR_WB_SAMPLE_ENTRY) AMR_WIDE_BAND else if (type == BoxTypes.EVRC_SAMPLE_ENTRY) EVRC else if (type == BoxTypes.EAC3_SAMPLE_ENTRY) EXTENDED_AC3 else if (type == BoxTypes.QCELP_SAMPLE_ENTRY) QCELP else if (type == BoxTypes.SMV_SAMPLE_ENTRY) SMV else UNKNOWN_AUDIO_CODEC
                return ac
            }
        }
    }

    private val smhd: SoundMediaHeaderBox
    private var sampleEntry: AudioSampleEntry? = null
    override lateinit var codec: Codec

    init {
        val mdia: Box = trak.getChild(BoxTypes.MEDIA_BOX)!!
        val minf: Box = mdia.getChild(BoxTypes.MEDIA_INFORMATION_BOX)!!
        smhd = minf.getChild(BoxTypes.SOUND_MEDIA_HEADER_BOX) as SoundMediaHeaderBox
        val stbl: Box = minf.getChild(BoxTypes.SAMPLE_TABLE_BOX)!!

        //sample descriptions: 'mp4a' and 'enca' have an ESDBox, all others have a CodecSpecificBox
        val stsd: SampleDescriptionBox = stbl.getChild(BoxTypes.SAMPLE_DESCRIPTION_BOX) as SampleDescriptionBox
        if (stsd.getChildren()[0] is AudioSampleEntry) {
            sampleEntry = stsd.getChildren()[0] as AudioSampleEntry
            val type: Long = sampleEntry!!.type
            if (sampleEntry!!.hasChild(BoxTypes.ESD_BOX)) findDecoderSpecificInfo(sampleEntry!!.getChild(BoxTypes.ESD_BOX) as ESDBox) else decoderInfo =
                net.sourceforge.jaad.mp4.api.DecoderInfo.parse(sampleEntry!!.getChildren()[0] as CodecSpecificBox)
            if (type == BoxTypes.ENCRYPTED_AUDIO_SAMPLE_ENTRY || type == BoxTypes.DRMS_SAMPLE_ENTRY) {
                findDecoderSpecificInfo(sampleEntry!!.getChild(BoxTypes.ESD_BOX) as ESDBox)
                protection =
                    Protection.Companion.parse(sampleEntry!!.getChild(BoxTypes.PROTECTION_SCHEME_INFORMATION_BOX))
                codec = protection!!.getOriginalFormat()!!
            } else codec = AudioCodec.forType(sampleEntry!!.type)
        } else {
            sampleEntry = null
            codec = AudioCodec.UNKNOWN_AUDIO_CODEC
        }
    }

    override val type: net.sourceforge.jaad.mp4.api.Type
        get() = net.sourceforge.jaad.mp4.api.Type.AUDIO

    fun getCodec(): Codec {
        return codec
    }

    val balance: Double
        /**
         * The balance is a floating-point number that places mono audio tracks in a
         * stereo space: 0 is centre (the normal value), full left is -1.0 and full
         * right is 1.0.
         *
         * @return the stereo balance for a this track
         */
        get() = smhd.balance
    val channelCount: Int
        /**
         * Returns the number of channels in this audio track.
         * @return the number of channels
         */
        get() = sampleEntry!!.channelCount
    val sampleRate: Int
        /**
         * Returns the sample rate of this audio track.
         * @return the sample rate
         */
        get() = sampleEntry!!.sampleRate
    val sampleSize: Int
        /**
         * Returns the sample size in bits for this track.
         * @return the sample size
         */
        get() = sampleEntry!!.sampleSize
    val volume: Double
        get() = tkhd.volume
}
