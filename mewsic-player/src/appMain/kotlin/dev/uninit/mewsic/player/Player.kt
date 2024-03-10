package dev.uninit.mewsic.player

import dev.uninit.mewsic.media.stream.MediaStream
import dev.uninit.mewsic.media.track.MediaTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.ffmpeg.global.avutil.av_log_set_level

abstract class Player {
    val queue = Queue()
    var currentTrack: MediaTrack? = null
    var gain = 1.0f
        set(value) {
            field = value
            currentStream?.gain = value
        }

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
    }
    open suspend fun pause() {
        // FIXME: Streams may get invalidated if paused for too long
        // Add some kind of timeout to set the stream to null?
        state = PlayerState.PAUSED
    }

    suspend fun setPlayIndex(index: Int) {
        val oldStream = currentStream
        currentTrack = queue.items.getOrNull(index)
        currentStream = currentTrack?.createStream()
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
//        private val logger = Logger.withPrefix("[libav]")
//
//        init {
//            logger.info("Setting up libav logging")
//            avutil.av_log_set_callback(this)
//        }
//
//        override fun call(level: Int, msg: BytePointer?) {
//            val message = msg?.string ?: "null"
//
//            when (level) {
//                avutil.AV_LOG_ERROR -> logger.error(message)
//                avutil.AV_LOG_WARNING -> logger.warn(message)
//                avutil.AV_LOG_INFO -> logger.info(message)
//                avutil.AV_LOG_DEBUG -> logger.debug(message)
//                avutil.AV_LOG_TRACE -> {}
//                avutil.AV_LOG_FATAL -> logger.critical(message)
//                avutil.AV_LOG_PANIC -> logger.critical(message)
//                avutil.AV_LOG_QUIET -> {}
//            }
//        }
//    }
}
