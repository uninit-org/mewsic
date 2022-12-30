package net.sourceforge.jaad.mp4.api
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.api.Track.Codec
import net.sourceforge.jaad.mp4.api.drm.ITunesProtection
import net.sourceforge.jaad.mp4.boxes.Box
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import net.sourceforge.jaad.mp4.boxes.impl.OriginalFormatBox
import net.sourceforge.jaad.mp4.boxes.impl.SchemeTypeBox

/**
 * This class contains information about a DRM system.
 */
abstract class Protection protected constructor(sinf: Box) {
    enum class Scheme(val type: Long) {
        ITUNES_FAIR_PLAY(1769239918), UNKNOWN(-1)
    }

    private var originalFormat: Codec? = null

    init {
        //original format
        val type: Long = (sinf.getChild(BoxTypes.ORIGINAL_FORMAT_BOX) as OriginalFormatBox).originalFormat
        var c: Codec?
        //TODO: currently it tests for audio and video codec, can do this any other way?
        if (!AudioTrack.AudioCodec.Companion.forType(type).also { c = it }.equals(AudioTrack.AudioCodec.UNKNOWN_AUDIO_CODEC)) originalFormat =
            c else if (net.sourceforge.jaad.mp4.api.VideoTrack.VideoCodec.Companion.forType(type).also { c = it } != net.sourceforge.jaad.mp4.api.VideoTrack.VideoCodec.UNKNOWN_VIDEO_CODEC) originalFormat =
            c else originalFormat = null
    }

    fun getOriginalFormat(): Codec? {
        return originalFormat
    }

    abstract val scheme: Scheme?

    //default implementation for unknown protection schemes
    private class UnknownProtection internal constructor(sinf: Box) : Protection(sinf) {
        override val scheme: Scheme
            get() = Scheme.UNKNOWN
       
       
    }

    companion object {
        fun parse(sinf: Box): Protection {
            var p: Protection? = null
            if (sinf.hasChild(BoxTypes.SCHEME_TYPE_BOX)) {
                val schm: SchemeTypeBox = sinf.getChild(BoxTypes.SCHEME_TYPE_BOX) as SchemeTypeBox
                val l: Long = schm.schemeType
                if (l == Scheme.ITUNES_FAIR_PLAY.type) p = ITunesProtection(sinf)
            }
            if (p == null) p = UnknownProtection(sinf)
            return p
        }
    }
}
