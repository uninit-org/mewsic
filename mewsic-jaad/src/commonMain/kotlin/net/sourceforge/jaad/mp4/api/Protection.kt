package net.sourceforge.jaad.mp4.api

import net.sourceforge.jaad.mp4.api.Track.Codec

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
        val type: Long = (sinf.getChild(BoxTypes.ORIGINAL_FORMAT_BOX) as OriginalFormatBox).getOriginalFormat()
        var c: Codec?
        //TODO: currently it tests for audio and video codec, can do this any other way?
        if (!AudioCodec.Companion.forType(type).also { c = it }.equals(AudioCodec.UNKNOWN_AUDIO_CODEC)) originalFormat =
            c else if (!net.sourceforge.jaad.mp4.api.VideoTrack.VideoCodec.Companion.forType(type).also { c = it }
                .equals(net.sourceforge.jaad.mp4.api.VideoTrack.VideoCodec.UNKNOWN_VIDEO_CODEC)) originalFormat =
            c else originalFormat = null
    }

    fun getOriginalFormat(): Codec? {
        return originalFormat
    }

    abstract val scheme: Scheme?

    //default implementation for unknown protection schemes
    private class UnknownProtection internal constructor(sinf: Box) : Protection(sinf) {
        override fun getScheme(): Scheme? {
            return Scheme.UNKNOWN
        }
    }

    companion object {
        fun parse(sinf: Box): Protection {
            var p: Protection? = null
            if (sinf.hasChild(BoxTypes.SCHEME_TYPE_BOX)) {
                val schm: SchemeTypeBox = sinf.getChild(BoxTypes.SCHEME_TYPE_BOX) as SchemeTypeBox
                val l: Long = schm.getSchemeType()
                if (l == Scheme.ITUNES_FAIR_PLAY.type) p = ITunesProtection(sinf)
            }
            if (p == null) p = UnknownProtection(sinf)
            return p
        }
    }
}
