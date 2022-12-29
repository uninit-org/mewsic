package net.sourceforge.jaad.mp4.api

import net.sourceforge.jaad.mp4.boxes.BoxTypes

/**
 * The `DecoderInfo` object contains the neccessary data to
 * initialize a decoder. A track either contains a `DecoderInfo` or a
 * byte-Array called the 'DecoderSpecificInfo', which is e.g. used for AAC.
 *
 * The `DecoderInfo` object received from a track is a subclass of
 * this class depending on the `Codec`.
 *
 * `
 * AudioTrack track = (AudioTrack) movie.getTrack(AudioCodec.AC3);
 * AC3DecoderInfo info = (AC3DecoderInfo) track.getDecoderInfo();
` *
 *
 * @author in-somnia
 */
object DecoderInfo {
    fun parse(css: net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox): DecoderInfo {
        val l: Long = css.getType()
        val info: DecoderInfo
        if (l == BoxTypes.H263_SPECIFIC_BOX) info =
            net.sourceforge.jaad.mp4.api.codec.H263DecoderInfo(css) else if (l == BoxTypes.AMR_SPECIFIC_BOX) info =
            net.sourceforge.jaad.mp4.api.codec.AMRDecoderInfo(css) else if (l == BoxTypes.EVRC_SPECIFIC_BOX) info =
            net.sourceforge.jaad.mp4.api.codec.EVRCDecoderInfo(css) else if (l == BoxTypes.QCELP_SPECIFIC_BOX) info =
            net.sourceforge.jaad.mp4.api.codec.QCELPDecoderInfo(css) else if (l == BoxTypes.SMV_SPECIFIC_BOX) info =
            net.sourceforge.jaad.mp4.api.codec.SMVDecoderInfo(css) else if (l == BoxTypes.AVC_SPECIFIC_BOX) info =
            net.sourceforge.jaad.mp4.api.codec.AVCDecoderInfo(css) else if (l == BoxTypes.AC3_SPECIFIC_BOX) info =
            net.sourceforge.jaad.mp4.api.codec.AC3DecoderInfo(css) else if (l == BoxTypes.EAC3_SPECIFIC_BOX) info =
            net.sourceforge.jaad.mp4.api.codec.EAC3DecoderInfo(css) else info = UnknownDecoderInfo()
        return info
    }

    private class UnknownDecoderInfo : DecoderInfo()
}
