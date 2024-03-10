package dev.uninit.mewsic.media.track

import dev.uninit.mewsic.media.stream.MediaStream
import dev.uninit.mewsic.utils.platform.logger
import org.bytedeco.ffmpeg.avcodec.AVPacket
import org.bytedeco.ffmpeg.avutil.AVDictionary
import org.bytedeco.ffmpeg.avutil.AVDictionaryEntry
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.ffmpeg.global.avformat
import org.bytedeco.ffmpeg.global.avutil
import java.io.OutputStream
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface MediaTrack {
    val artist: String
    val title: String
    val thumbnailBase64: String?
    val album: String?
    val albumArtist: String?
    val albumTrack: Int?
    val albumTrackTotal: Int?
    val year: Int?
    val duration: Long

    fun isValid(): Boolean {
        return duration > 0
    }

    suspend fun createStream(): MediaStream

    suspend fun saveTo(out: OutputStream) {
        // TODO: Set up libav to convert from raw pcm to m4a
        // TODO: Maybe set up a library to wrap common libav operations?
        TODO("Not yet implemented")
    }

    companion object {
        class MediaQueryResults(
            val artist: String?,
            val title: String?,
            val album: String?,
            val albumArtist: String?,
            val albumArtBase64: String?,
            val albumTrack: Int? = null,
            val albumTrackTotal: Int? = null,
            val year: Int? = null,
            val duration: Long?
        )

        @OptIn(ExperimentalEncodingApi::class)
        fun collectMetadata(url: String): MediaQueryResults {
            var artist: String? = null
            var title: String? = null
            var album: String? = null
            var albumArtist: String? = null
            var albumArtBase64: String? = null
            var albumTrack: Int? = null
            var albumTrackTotal: Int? = null
            var year: Int? = null
            var duration: Long? = null

            val ctx = avformat.avformat_alloc_context()
            try {
                logger.debug("Opening data: $url")
                if (avformat.avformat_open_input(ctx, url, null, null) == 0) {
                    try {
                        logger.debug("Finding stream info")
                        if (avformat.avformat_find_stream_info(ctx, null as AVDictionary?) == 0) {
                            duration = ctx.duration() * 44100 / avutil.AV_TIME_BASE

                            // Collect metadata
                            logger.debug("Collecting metadata")
                            if (ctx.metadata() != null) {
                                var tag: AVDictionaryEntry? = null
                                while (true) {
                                    tag = avutil.av_dict_get(ctx.metadata(), "", tag, avutil.AV_DICT_IGNORE_SUFFIX)
                                    if (tag == null) break
                                    logger.debug("Metadata: ${tag.key().string} = ${tag.value().string}")

                                    when (tag.key().string.lowercase(Locale.getDefault())) {
                                        "artist" -> artist = tag.value().string
                                        "title" -> title = tag.value().string
                                        "album" -> album = tag.value().string
                                        "album_artist" -> albumArtist = tag.value().string
                                        "track" -> {
                                            if ("/" in tag.value().string) {
                                                val (track, total) = tag.value().string.split("/")
                                                albumTrack = track.toIntOrNull()
                                                albumTrackTotal = total.toIntOrNull()
                                            } else {
                                                albumTrack = tag.value().string.toIntOrNull()
                                            }
                                        }

                                        "tracktotal" -> albumTrackTotal = tag.value().string.toIntOrNull()
                                        "year" -> year = tag.value().string.toIntOrNull()
                                        "date" -> year = tag.value().string.toIntOrNull()
                                    }
                                }
                            }

                            if (albumArtist == null) {
                                albumArtist = artist
                            }

                            // Find video stream
                            logger.debug("Finding video stream")
                            for (i in 0 until ctx.nb_streams()) {
                                val stream = ctx.streams(i)
                                val codec = stream.codecpar()
                                if (codec.codec_type() == avutil.AVMEDIA_TYPE_VIDEO) {
                                    val frame = AVPacket()
                                    avformat.av_read_frame(ctx, frame)
                                    val arr = ByteArray(frame.size())
                                    frame.data().get(arr)
                                    albumArtBase64 = Base64.encode(arr)
                                    avcodec.av_packet_unref(frame)
                                    break
                                }
                            }
                        } else {
                            logger.error("Failed to find stream info: $url")
                        }
                    } finally {
                        avformat.avformat_close_input(ctx)
                    }
                } else {
                    logger.error("Failed to open data: $url")
                }
            } finally {
                avformat.avformat_free_context(ctx)
            }

            return MediaQueryResults(artist, title, album, albumArtist, albumArtBase64, albumTrack, albumTrackTotal, year, duration)
        }
    }
}
