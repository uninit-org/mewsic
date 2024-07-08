package dev.uninit.mewsic.player

import dev.uninit.mewsic.media.stream.MediaStream
import dev.uninit.mewsic.media.track.MediaTrack
import dev.uninit.mewsic.utils.platform.makeLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.ffmpeg.global.avutil.av_log_set_level
import kotlin.time.Duration.Companion.minutes

abstract class Player {
    private val logger = makeLogger()
    val queue = Queue()
    var currentTrack: MediaTrack? = null
    var gain = 1.0f
        set(value) {
            field = value
            currentStream?.gain = value
        }
    var autoPlayEnabled = true
    protected var lastPlay = Instant.DISTANT_PAST

    protected var currentStream: MediaStream? = null
        set(value) {
            field?.dispose()
            field = value
            value?.gain = gain
        }
    var state: PlayerState = PlayerState.UNINITIALIZED
        protected set
    val offset: Long
        get() = currentStream?.offset ?: -1

    open suspend fun play() {
        state = PlayerState.PLAYING
        if (currentStream == null) {
            setPlayIndex(0)
        }

        if (currentStream != null && lastPlay != Instant.DISTANT_PAST && lastPlay - Clock.System.now() > 1.minutes) {
            logger.info("Resuming from long pause, reloading stream")
            val offset = currentStream!!.offset
            val newStream = currentTrack!!.createStream()
            newStream.seekTo(offset)
            currentStream = newStream
        }
    }
    open suspend fun pause() {
        // FIXME: Streams may get invalidated if paused for too long
        // Should be fixed.

        if (state == PlayerState.PAUSED) {
            logger.warn("pause() invoked with state already set to pause")
            return
        }
        lastPlay = Clock.System.now()
        state = PlayerState.PAUSED
    }

    suspend fun setPlayIndex(index: Int) {
        val oldStream = currentStream
        currentTrack = queue.items.getOrNull(index)
        currentStream = currentTrack?.createStream()
        lastPlay = Instant.DISTANT_PAST
        oldStream?.dispose()
    }

    suspend fun nextTrack() {
        val index = queue.items.indexOf(currentTrack)
        if (index == -1) {
            return
        }

        val nextIndex = when (queue.loopMode) {
            LoopMode.OFF -> (index + 1)  // TODO: Check if autoplay is enabled
            LoopMode.ONE -> index
            LoopMode.ALL -> (index + 1) % queue.items.size
        }

        if (autoPlayEnabled && nextIndex > queue.items.lastIndex) {
            queue.items.add(currentTrack!!.provider.getRelatedTracks(currentTrack!!).first())
        }

        setPlayIndex(nextIndex)
    }

    suspend fun previousTrack() {
        val index = queue.items.indexOf(currentTrack)
        if (index == -1) {
            return
        }

        val previousIndex = when (queue.loopMode) {
            LoopMode.OFF -> (index - 1)
            LoopMode.ONE -> index
            LoopMode.ALL -> (index - 1 + queue.items.size) % queue.items.size
        }

        setPlayIndex(previousIndex)
    }

    protected abstract suspend fun loop()

    fun spawn(scope: CoroutineScope = GlobalScope) {
        scope.launch(Dispatchers.IO) {
            if (state == PlayerState.UNINITIALIZED) {
                state = PlayerState.STOPPED
                loop()
            }
        }
    }

    suspend fun seekTo(position: Long) {
        currentStream?.seekTo(position)
    }

    companion object {
        init {
            av_log_set_level(avutil.AV_LOG_ERROR)
        }
    }

    // FIXME: This crashes for some reason?
//    companion object : LogCallback() {
//        private val log = log.withPrefix("[libav]")
//
//        init {
//            log.info("Setting up libav logging")
//            avutil.av_log_set_callback(this)
//        }
//
//        override fun call(level: Int, msg: BytePointer?) {
//            val message = msg?.string ?: "null"
//
//            when (level) {
//                avutil.AV_LOG_ERROR -> log.error(message)
//                avutil.AV_LOG_WARNING -> log.warn(message)
//                avutil.AV_LOG_INFO -> log.info(message)
//                avutil.AV_LOG_DEBUG -> log.debug(message)
//                avutil.AV_LOG_TRACE -> {}
//                avutil.AV_LOG_FATAL -> log.critical(message)
//                avutil.AV_LOG_PANIC -> log.critical(message)
//                avutil.AV_LOG_QUIET -> {}
//            }
//        }
//    }
}
