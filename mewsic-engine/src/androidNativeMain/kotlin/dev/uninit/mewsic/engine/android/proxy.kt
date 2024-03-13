package dev.uninit.mewsic.engine.android

import android.audio.*
import dev.uninit.mewsic.engine.Disposable
import dev.uninit.mewsic.engine.Engine
import dev.uninit.mewsic.engine.EngineProperty
import dev.uninit.mewsic.engine.Errno
import dev.uninit.mewsic.engine.android.AndroidEnginePostprocessor
import dev.uninit.mewsic.engine.android.AndroidEnginePreprocessor
import kotlinx.cinterop.*
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
internal class AndroidEngineProxy : Disposable {
    private val engine = Engine()
    private val config = nativeHeap.alloc<effect_config_t>()

    private var blockSize = 1024
        set(value) {
            if (value == field) return
            field = value
            leftBufferIn = FloatArray(value) { 0f }
            rightBufferIn = FloatArray(value) { 0f }
            leftBufferOut = FloatArray(value) { 0f }
            rightBufferOut = FloatArray(value) { 0f }
        }

    private var leftBufferIn = FloatArray(blockSize) { 0f }
    private var rightBufferIn = FloatArray(blockSize) { 0f }
    private var leftBufferOut = FloatArray(blockSize) { 0f }
    private var rightBufferOut = FloatArray(blockSize) { 0f }

    // TODO: Implement preprocessor and postprocessor
    private lateinit var preprocessor: AndroidEnginePreprocessor
    private lateinit var postprocessor: AndroidEnginePostprocessor

    fun process(inBuffer: CPointer<audio_buffer_t>?, outBuffer: CPointer<audio_buffer_t>?): Errno {
        if (!engine.getProperty(EngineProperty.GlobalEnabled)) {
            return Errno.ENODATA
        }

        var errno = preprocessor.process(inBuffer ?: config.inputCfg.buffer.ptr, leftBufferIn, rightBufferIn)
        if (errno != Errno.OK) {
            return errno
        }

        errno = engine.process(leftBufferIn, rightBufferIn, leftBufferOut, rightBufferOut)
        if (errno != Errno.OK) {
            return errno
        }

        return postprocessor.process(leftBufferOut, rightBufferOut, outBuffer ?: config.outputCfg.buffer.ptr)
    }

    @OptIn(UnsafeNumber::class)
    fun command(cmdCode: effect_command_e, cmdSize: uint32_t, pCmdData: COpaquePointer?, pReplySize: CPointer<uint32_tVar>?, pReplyData: COpaquePointer?): Errno {
        val replySize = pReplySize?.pointed ?: 0u

        when (cmdCode) {
            EFFECT_CMD_INIT -> {
                if (replySize != sizeOf<int32_tVar>() || pReplyData == null) {
                    return Errno.EINVAL
                }

                pReplyData.reinterpret<int32_tVar>().pointed.value = Errno.OK.errCode
            }

            EFFECT_CMD_SET_CONFIG -> {
                if (cmdSize != sizeOf<effect_config_t>().convert<uint32_t>() || pCmdData == null || replySize != sizeOf<int32_tVar>() || pReplyData == null) {
                    return Errno.EINVAL
                }

                // TODO: Validate fields

                memcpy(config.ptr, pCmdData, sizeOf<effect_config_t>().convert())

                // TODO: Set preprocessor and postprocessor
            }

            EFFECT_CMD_RESET -> {
                // TODO
                engine.reset()
            }

            EFFECT_CMD_ENABLE -> {
                if (replySize != sizeOf<int32_tVar>() || pReplyData == null) {
                    return Errno.EINVAL
                }

                pReplyData.reinterpret<int32_tVar>().pointed.value = Errno.OK.errCode
            }

            EFFECT_CMD_DISABLE -> {
                if (replySize != sizeOf<int32_tVar>() || pReplyData == null) {
                    return Errno.EINVAL
                }

                pReplyData.reinterpret<int32_tVar>().pointed.value = Errno.OK.errCode
            }

            EFFECT_CMD_SET_PARAM -> {
                // TODO
            }

            EFFECT_CMD_GET_PARAM -> {
                // TODO
            }

            EFFECT_CMD_GET_CONFIG -> {
                // TODO
            }

            else -> {
                return Errno.EINVAL
            }
        }

        return Errno.OK
    }

    override fun dispose() {
        engine.dispose()

        nativeHeap.free(config.ptr)
    }
}
