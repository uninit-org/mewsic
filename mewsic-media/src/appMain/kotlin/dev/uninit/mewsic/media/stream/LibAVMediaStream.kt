package dev.uninit.mewsic.media.stream

import dev.uninit.mewsic.utils.platform.logger
import org.bytedeco.ffmpeg.avformat.AVFormatContext
import org.bytedeco.ffmpeg.avutil.AVDictionary
import org.bytedeco.ffmpeg.avutil.AVFrame
import org.bytedeco.ffmpeg.global.avcodec.*
import org.bytedeco.ffmpeg.global.avfilter.*
import org.bytedeco.ffmpeg.global.avformat.*
import org.bytedeco.ffmpeg.global.avutil.*
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class LibAVMediaStream(private val url: String) : MediaStream {
    init {
        logger.debug("Opening stream: $url")
    }

    private val isLinuxOrAndroid = System.getProperty("os.name").lowercase(Locale.getDefault()).let {
//        it.contains("linux") ||
                it.contains("android")
    }

    private val fmtCtx = avformat_alloc_context().also {
        if (avformat_open_input(it, url, null, null) != 0) {
            avformat_free_context(it)
            throw IllegalStateException("Failed to open input")
        }
        if (avformat_find_stream_info(it, null as AVDictionary?) != 0) {
            avformat_close_input(it)
            avformat_free_context(it)
            throw IllegalStateException("Failed to find stream info")
        }
    }

    private val audioStreamIdx = fmtCtx.audioStream()

    private fun AVFormatContext.audioStream(): Int {
        for (i in 0 until nb_streams()) {
            val stream = streams(i)
            val codec = stream.codecpar()

            if (codec.codec_type() == AVMEDIA_TYPE_AUDIO){
                return i
            }
        }

        throw IllegalStateException("No audio stream found")
    }
    private val stream = fmtCtx.streams(audioStreamIdx)
    private val codecParam = stream.codecpar()
    private val codec = avcodec_find_decoder(codecParam.codec_id())
    private val ctx = avcodec_alloc_context3(codec).also {
        if (avcodec_parameters_to_context(it, codecParam) != 0) {
            avcodec_free_context(it)
            avformat_close_input(fmtCtx)
            avformat_free_context(fmtCtx)
            throw IllegalStateException("Failed to copy codec params to context")
        }
        if (avcodec_open2(it, codec, null as AVDictionary?) != 0) {
            avcodec_free_context(it)

            avformat_close_input(fmtCtx)
            avformat_free_context(fmtCtx)
            throw IllegalStateException("Failed to open codec")
        }
    }

    private val graph = avfilter_graph_alloc()
    private val abufferCtx = avfilter_graph_alloc_filter(
        graph,
        avfilter_get_by_name("abuffer"),
        "src"
    ).also {
        val buf = ByteArray(64)
        av_channel_layout_describe(ctx.ch_layout(), buf, buf.size.toLong())
        av_opt_set(it, "channel_layout", buf.decodeToString(), AV_OPT_SEARCH_CHILDREN)
        av_opt_set(
            it,
            "sample_fmt",
            av_get_sample_fmt_name(ctx.sample_fmt()).string,
            AV_OPT_SEARCH_CHILDREN
        )
        av_opt_set(
            it,
            "sample_rate",
            ctx.sample_rate().toString(),
            AV_OPT_SEARCH_CHILDREN
        )
        avfilter_init_str(it, null as String?)
    }
    private val aformatCtx = avfilter_graph_alloc_filter(
        graph,
        avfilter_get_by_name("aformat"),
        "aformat"
    ).also {
        av_opt_set(
            it,
            "sample_fmts",
            av_get_sample_fmt_name(if (isLinuxOrAndroid) AV_SAMPLE_FMT_FLTP else AV_SAMPLE_FMT_S16P).string,
            AV_OPT_SEARCH_CHILDREN
        )
        av_opt_set(it, "sample_rates", "44100", AV_OPT_SEARCH_CHILDREN)
        av_opt_set(it, "channel_layouts", "stereo", AV_OPT_SEARCH_CHILDREN)
        avfilter_init_str(it, null as String?)
    }
    private val abuffersinkCtx = avfilter_graph_alloc_filter(
        graph,
        avfilter_get_by_name("abuffersink"),
        "sink"
    ).also {
        avfilter_init_str(it, null as String?)
    }


    private val frame = av_frame_alloc()
    private val sampleSize = if (isLinuxOrAndroid) 4 else 2
    private val packet = av_packet_alloc().also {
        av_new_packet(it, sampleSize * 2 * 128)
    }

    init {
        avfilter_link(abufferCtx, 0, aformatCtx, 0)
        avfilter_link(aformatCtx, 0, abuffersinkCtx, 0)

        avfilter_graph_config(graph, null)
    }

    private var invalidated = false

    private var overflow: ByteArray? = null
    private var overflowBuffer: ByteBuffer? = null

    private fun AVFrame.readIntoFloat(buffer: ByteBuffer): Int {
        var count = 0
        var overflowStart = 0
        val nb_samples = nb_samples()
        val nb_channels = ch_layout().nb_channels()
        var didOverflow = false

        val channelData = (0 until nb_channels).map { extended_data(it) }

        for (i in 0 until nb_samples) {
            for (j in 0 until nb_channels) {
                val channel = channelData[j]
                val formatted = channel.getFloat(i * sampleSize.toLong()) * gain
                try {
                    if (didOverflow) {
                        overflowBuffer!!.putFloat(formatted)
                    } else {
                        buffer.putFloat(formatted)
                    }
                } catch (e: BufferOverflowException) {
                    didOverflow = true
                    if (overflow == null) {
                        overflow = ByteArray(nb_samples * nb_channels * sampleSize)
                        overflowBuffer = ByteBuffer.wrap(overflow!!)
                            .also { it.order(ByteOrder.BIG_ENDIAN) }
                        overflowStart = count
                    }
                    overflowBuffer!!.putFloat(formatted)
                }
                count++
            }
        }
        overflow = overflow?.copyOfRange(0, (count - overflowStart) * sampleSize)
        if (overflowStart == 0) {
            return count * sampleSize
        }
        return (overflowStart) * sampleSize
    }

    private fun AVFrame.readIntoShort(buffer: ByteBuffer): Int {
        var count = 0
        var overflowStart = 0
        val nb_samples = nb_samples()
        val nb_channels = ch_layout().nb_channels()
        var didOverflow = false

        val channelData = (0 until nb_channels).map { extended_data(it) }

        for (i in 0 until nb_samples) {
            for (j in 0 until nb_channels) {
                val channel = channelData[j]
                val formatted = (channel.getShort(i * sampleSize.toLong()) * gain).toInt().toShort()
                try {
                    if (didOverflow) {
                        overflowBuffer!!.putShort(formatted)
                    } else {
                        buffer.putShort(formatted)
                    }
                } catch (e: BufferOverflowException) {
                    didOverflow = true
                    if (overflow == null) {
                        overflow = ByteArray(nb_samples * nb_channels * sampleSize)
                        overflowBuffer = ByteBuffer.wrap(overflow!!)
                            .also { it.order(ByteOrder.BIG_ENDIAN) }
                        overflowStart = count
                    }
                    overflowBuffer!!.putShort(formatted)
                }
                count++
            }
        }
        overflow = overflow?.copyOfRange(0, (count - overflowStart) * sampleSize)
        if (overflowStart == 0) {
            return count * sampleSize
        }
        return (overflowStart) * sampleSize
    }

    private val length = fmtCtx.duration() * 44100 / AV_TIME_BASE

    override var offset = 0L
    override var gain = 1.0f
    override suspend fun seekTo(position: Long) {
        if (position < 0 || position > length) {
            throw IllegalArgumentException("Invalid offset: $position")
        }

        if (position != this.offset) {
            this.offset = position
            av_seek_frame(
                fmtCtx,
                audioStreamIdx,
                position,
                AVSEEK_FLAG_ANY
            )
        }
    }

    override suspend fun copyInto(buffer: ByteArray, offset: Int, length: Int): Int {
        var total = 0
        val wrapped = ByteBuffer.wrap(buffer, offset, length).also { it.order(ByteOrder.BIG_ENDIAN) }

        if (overflow != null) {
            val overflowSize = overflow!!.size
            if (overflowSize > length) {
                overflow!!.copyInto(buffer, offset, 0, length)
                overflow = overflow!!.copyOfRange(length, overflowSize)
                overflowBuffer = ByteBuffer.wrap(overflow!!).also { it.order(ByteOrder.BIG_ENDIAN) }
                return length
            } else {
                overflow!!.copyInto(buffer, offset, 0, overflowSize)
                wrapped.position(overflowSize)
                total += overflowSize
                overflow = null
                overflowBuffer = null
            }
        }

        while (total < length) {
            if (invalidated) {
                return -1
            }

            if (av_read_frame(fmtCtx, packet) != 0) {
                logger.info("Failed to read frame, likely EOF")
                break
            }

            if (packet.stream_index() != audioStreamIdx) {
                logger.debug("Ignoring non-audio packet")
                av_packet_unref(packet)
                continue
            }

            if (avcodec_send_packet(ctx, packet) == 0) {
                av_packet_unref(packet)
            } else {
                logger.error("Failed to send packet")
                av_packet_unref(packet)
                break
            }

            while (avcodec_receive_frame(ctx, frame) == 0) {
                if (invalidated) {
                    return -1
                }

                av_buffersrc_add_frame(abufferCtx, frame)
                av_frame_unref(frame)

                while (av_buffersink_get_frame(abuffersinkCtx, frame) >= 0) {
                    val written = if (isLinuxOrAndroid) frame.readIntoFloat(wrapped) else frame.readIntoShort(wrapped)
                    total += written

                    av_frame_unref(frame)
                }
            }
        }

        if (total == 0) {
            return -1  // EOF
        }

        return total
    }

    private var disposed = false

    override fun dispose() {
        if (disposed) return
        disposed = true

        logger.debug("Disposing")

        avcodec_close(ctx)
        avcodec_free_context(ctx)
        avformat_close_input(fmtCtx)
        avformat_free_context(fmtCtx)
        av_frame_free(frame)
        av_packet_free(packet)
        avfilter_graph_free(graph)
    }
}
