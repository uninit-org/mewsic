package net.sourceforge.jaad.mp4.api
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
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
abstract class DecoderInfo {
    fun parse(css: net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox): DecoderInfo {
        val l: Long = css.type
        val info: DecoderInfo
        when (l) {
            BoxTypes.H263_SPECIFIC_BOX -> info =
                net.sourceforge.jaad.mp4.api.codec.H263DecoderInfo(css)
            BoxTypes.AMR_SPECIFIC_BOX -> info =
                net.sourceforge.jaad.mp4.api.codec.AMRDecoderInfo(css)
            BoxTypes.EVRC_SPECIFIC_BOX -> info =
                net.sourceforge.jaad.mp4.api.codec.EVRCDecoderInfo(css)
            BoxTypes.QCELP_SPECIFIC_BOX -> info =
                net.sourceforge.jaad.mp4.api.codec.QCELPDecoderInfo(css)
            BoxTypes.SMV_SPECIFIC_BOX -> info =
                net.sourceforge.jaad.mp4.api.codec.SMVDecoderInfo(css)
            BoxTypes.AVC_SPECIFIC_BOX -> info =
                net.sourceforge.jaad.mp4.api.codec.AVCDecoderInfo(css)
            BoxTypes.AC3_SPECIFIC_BOX -> info =
                net.sourceforge.jaad.mp4.api.codec.AC3DecoderInfo(css)
            BoxTypes.EAC3_SPECIFIC_BOX -> info =
                net.sourceforge.jaad.mp4.api.codec.EAC3DecoderInfo(css)
            else -> info = UnknownDecoderInfo()
        }
        return info
    }
    companion object : DecoderInfo(){

    }

    private class UnknownDecoderInfo : DecoderInfo()
}
