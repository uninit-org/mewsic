package net.sourceforge.jaad.mp4.api

import net.sourceforge.jaad.mp4.MP4InputStream

class VideoTrack(trak: Box, `in`: MP4InputStream) : Track(trak, `in`) {
    enum class VideoCodec : Codec {
        AVC, H263, MP4_ASP, UNKNOWN_VIDEO_CODEC;

        companion object {
            fun forType(type: Long): Codec {
                val ac: Codec
                ac =
                    if (type == BoxTypes.AVC_SAMPLE_ENTRY) AVC else if (type == BoxTypes.H263_SAMPLE_ENTRY) H263 else if (type == BoxTypes.MP4V_SAMPLE_ENTRY) MP4_ASP else UNKNOWN_VIDEO_CODEC
                return ac
            }
        }
    }

    private val vmhd: VideoMediaHeaderBox
    private var sampleEntry: VideoSampleEntry? = null
    private override var codec: Codec? = null

    init {
        val minf: Box = trak.getChild(BoxTypes.MEDIA_BOX).getChild(BoxTypes.MEDIA_INFORMATION_BOX)
        vmhd = minf.getChild(BoxTypes.VIDEO_MEDIA_HEADER_BOX) as VideoMediaHeaderBox
        val stbl: Box = minf.getChild(BoxTypes.SAMPLE_TABLE_BOX)

        //sample descriptions: 'mp4v' has an ESDBox, all others have a CodecSpecificBox
        val stsd: SampleDescriptionBox = stbl.getChild(BoxTypes.SAMPLE_DESCRIPTION_BOX) as SampleDescriptionBox
        if (stsd.getChildren().get(0) is VideoSampleEntry) {
            sampleEntry = stsd.getChildren().get(0) as VideoSampleEntry
            val type: Long = sampleEntry.getType()
            if (type == BoxTypes.MP4V_SAMPLE_ENTRY) findDecoderSpecificInfo(sampleEntry.getChild(BoxTypes.ESD_BOX) as ESDBox) else if (type == BoxTypes.ENCRYPTED_VIDEO_SAMPLE_ENTRY || type == BoxTypes.DRMS_SAMPLE_ENTRY) {
                findDecoderSpecificInfo(sampleEntry.getChild(BoxTypes.ESD_BOX) as ESDBox)
                protection =
                    Protection.Companion.parse(sampleEntry.getChild(BoxTypes.PROTECTION_SCHEME_INFORMATION_BOX))
            } else decoderInfo =
                net.sourceforge.jaad.mp4.api.DecoderInfo.parse(sampleEntry.getChildren().get(0) as CodecSpecificBox)
            codec = VideoCodec.forType(sampleEntry.getType())
        } else {
            sampleEntry = null
            codec = VideoCodec.UNKNOWN_VIDEO_CODEC
        }
    }

    override val type: net.sourceforge.jaad.mp4.api.Type
        get() = net.sourceforge.jaad.mp4.api.Type.VIDEO

    override fun getCodec(): Codec? {
        return codec
    }

    val width: Int
        get() = if (sampleEntry != null) sampleEntry.getWidth() else 0
    val height: Int
        get() = if (sampleEntry != null) sampleEntry.getHeight() else 0
    val horizontalResolution: Double
        get() = if (sampleEntry != null) sampleEntry.getHorizontalResolution() else 0
    val verticalResolution: Double
        get() = if (sampleEntry != null) sampleEntry.getVerticalResolution() else 0
    val frameCount: Int
        get() = if (sampleEntry != null) sampleEntry.getFrameCount() else 0
    val compressorName: String
        get() = if (sampleEntry != null) sampleEntry.getCompressorName() else ""
    val depth: Int
        get() = if (sampleEntry != null) sampleEntry.getDepth() else 0
    val layer: Int
        get() = tkhd.getLayer()
}
