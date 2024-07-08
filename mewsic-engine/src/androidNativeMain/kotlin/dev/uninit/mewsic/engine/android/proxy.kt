package dev.uninit.mewsic.engine.android

import android.audio.*
import dev.uninit.mewsic.engine.Disposable
import dev.uninit.mewsic.engine.Engine
import dev.uninit.mewsic.engine.EngineProperty
import dev.uninit.mewsic.engine.Errno
import dev.uninit.mewsic.utils.platform.makeLogger
import kotlinx.cinterop.*
import platform.posix.int32_tVar
import platform.posix.memcpy
import platform.posix.uint32_t
import platform.posix.uint32_tVar

@OptIn(ExperimentalForeignApi::class)
internal class AndroidEngineProxy : Disposable {
    private val engine = Engine()
    private val config = nativeHeap.alloc<effect_config_t>()
    private val logger = makeLogger()

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
                    logger.error("EFFECT_CMD_INIT called with invalid replySize = $replySize, pReplyData = $pReplyData, expected replySize = ${sizeOf<int32_tVar>()}")
                    return Errno.EINVAL.also { engine.setProperty(EngineProperty.GlobalEnabled, false) }
                }

                pReplyData.reinterpret<int32_tVar>().pointed.value = Errno.OK.errCode
            }

            EFFECT_CMD_SET_CONFIG -> {
                if (cmdSize != sizeOf<effect_config_t>().convert<uint32_t>() || pCmdData == null || replySize != sizeOf<int32_tVar>() || pReplyData == null) {
                    logger.error("EFFECT_CMD_SET_CONFIG called with invalid cmdSize = $cmdSize, pCmdData = $pCmdData, replySize = $replySize, pReplyData = $pReplyData, expected cmdSize = ${sizeOf<effect_config_t>()}, replySize = ${sizeOf<int32_tVar>()}")
                    return Errno.EINVAL.also { engine.setProperty(EngineProperty.GlobalEnabled, false) }
                }

                memScoped {
                    val tmpConfig = alloc<effect_config_t>()
                    // TODO: Validate fields

                    if (tmpConfig.inputCfg.channels and AUDIO_CHANNEL_IN_STEREO.inv() != 0u ||
                        tmpConfig.outputCfg.channels and AUDIO_CHANNEL_OUT_STEREO.inv() != 0u) {
                        return Errno.EINVAL.also { engine.setProperty(EngineProperty.GlobalEnabled, false) }
                    }

                    if (tmpConfig.inputCfg.buffer.frameCount != tmpConfig.outputCfg.buffer.frameCount) {
                        return Errno.EINVAL.also { engine.setProperty(EngineProperty.GlobalEnabled, false) }
                    }

                    val validFormats = arrayOf(
                        AUDIO_FORMAT_PCM_FLOAT,
                        AUDIO_FORMAT_PCM_32_BIT,
                        AUDIO_FORMAT_PCM_16_BIT,
                        AUDIO_FORMAT_PCM_8_BIT
                    )

                    if (tmpConfig.inputCfg.format.convert<audio_format_t>() !in validFormats ||
                        tmpConfig.outputCfg.format.convert<audio_format_t>() !in validFormats) {
                        return Errno.EINVAL.also { engine.setProperty(EngineProperty.GlobalEnabled, false) }
                    }
                }

                blockSize = config.inputCfg.buffer.frameCount.convert()

                memcpy(config.ptr, pCmdData, sizeOf<effect_config_t>().convert())

                val stereoIn = config.inputCfg.channels and AUDIO_CHANNEL_IN_STEREO == AUDIO_CHANNEL_IN_STEREO
                preprocessor = if (stereoIn) {
                    when (config.inputCfg.format.convert<audio_format_t>()) {
                        AUDIO_FORMAT_PCM_FLOAT -> AndroidEnginePreprocessor { buffer, inLeft, inRight ->
                            val buf = buffer.pointed.f32!!
                            for (i in 0 until blockSize) {
                                inLeft[i] = buf[i * 2]
                                inRight[i] = buf[i * 2 + 1]
                            }
                            Errno.OK
                        }
                        AUDIO_FORMAT_PCM_32_BIT -> AndroidEnginePreprocessor { buffer, inLeft, inRight ->
                            val buf = buffer.pointed.s32!!
                            for (i in 0 until blockSize) {
                                inLeft[i] = buf[i * 2].toFloat() / Int.MAX_VALUE
                                inRight[i] = buf[i * 2 + 1].toFloat() / Int.MAX_VALUE
                            }
                            Errno.OK
                        }
                        AUDIO_FORMAT_PCM_16_BIT -> AndroidEnginePreprocessor { buffer, inLeft, inRight ->
                            val buf = buffer.pointed.s16!!
                            for (i in 0 until blockSize) {
                                inLeft[i] = buf[i * 2].toFloat() / Short.MAX_VALUE
                                inRight[i] = buf[i * 2 + 1].toFloat() / Short.MAX_VALUE
                            }
                            Errno.OK
                        }
                        AUDIO_FORMAT_PCM_8_BIT -> AndroidEnginePreprocessor { buffer, inLeft, inRight ->
                            val buf = buffer.pointed.u8!!
                            for (i in 0 until blockSize) {
                                inLeft[i] = buf[i * 2].toByte().toFloat() / Byte.MAX_VALUE
                                inRight[i] = buf[i * 2 + 1].toByte().toFloat() / Byte.MAX_VALUE
                            }
                            Errno.OK
                        }
                        else -> return Errno.EINVAL.also { engine.setProperty(EngineProperty.GlobalEnabled, false) }
                    }
                } else {
                    when (config.inputCfg.format.convert<audio_format_t>()) {
                        AUDIO_FORMAT_PCM_FLOAT -> AndroidEnginePreprocessor { buffer, inLeft, inRight ->
                            val buf = buffer.pointed.f32!!
                            for (i in inLeft.indices) {
                                inLeft[i] = buf[i]
                                inRight[i] = buf[i]
                            }
                            Errno.OK
                        }
                        AUDIO_FORMAT_PCM_32_BIT -> AndroidEnginePreprocessor { buffer, inLeft, inRight ->
                            val buf = buffer.pointed.s32!!
                            for (i in inLeft.indices) {
                                inLeft[i] = buf[i].toFloat() / Int.MAX_VALUE
                                inRight[i] = buf[i].toFloat() / Int.MAX_VALUE
                            }
                            Errno.OK
                        }
                        AUDIO_FORMAT_PCM_16_BIT -> AndroidEnginePreprocessor { buffer, inLeft, inRight ->
                            val buf = buffer.pointed.s16!!
                            for (i in inLeft.indices) {
                                inLeft[i] = buf[i].toFloat() / Short.MAX_VALUE
                                inRight[i] = buf[i].toFloat() / Short.MAX_VALUE
                            }
                            Errno.OK
                        }
                        AUDIO_FORMAT_PCM_8_BIT -> AndroidEnginePreprocessor { buffer, inLeft, inRight ->
                            val buf = buffer.pointed.u8!!
                            for (i in inLeft.indices) {
                                inLeft[i] = buf[i].toFloat() / UByte.MAX_VALUE.toInt()
                                inRight[i] = buf[i].toFloat() / UByte.MAX_VALUE.toInt()
                            }
                            Errno.OK
                        }
                        else -> return Errno.EINVAL.also { engine.setProperty(EngineProperty.GlobalEnabled, false) }
                    }
                }

                val stereoOut = config.outputCfg.channels and AUDIO_CHANNEL_OUT_STEREO == AUDIO_CHANNEL_OUT_STEREO
                postprocessor = if (stereoOut) {
                    when (config.outputCfg.format.convert<audio_format_t>()) {
                        AUDIO_FORMAT_PCM_FLOAT -> AndroidEnginePostprocessor { outLeft, outRight, buffer ->
                            val buf = buffer.pointed.f32!!
                            for (i in outLeft.indices) {
                                buf[i * 2] = outLeft[i]
                                buf[i * 2 + 1] = outRight[i]
                            }
                            Errno.OK
                        }
                        AUDIO_FORMAT_PCM_32_BIT -> AndroidEnginePostprocessor { outLeft, outRight, buffer ->
                            val buf = buffer.pointed.s32!!
                            for (i in outLeft.indices) {
                                buf[i * 2] = (outLeft[i] * Short.MAX_VALUE).toInt()
                                buf[i * 2 + 1] = (outRight[i] * Short.MAX_VALUE).toInt()
                            }
                            Errno.OK
                        }
                        AUDIO_FORMAT_PCM_16_BIT -> AndroidEnginePostprocessor { outLeft, outRight, buffer ->
                            val buf = buffer.pointed.s16!!
                            for (i in outLeft.indices) {
                                buf[i * 2] = (outLeft[i] * Short.MAX_VALUE).toInt().toShort()
                                buf[i * 2 + 1] = (outRight[i] * Short.MAX_VALUE).toInt().toShort()
                            }
                            Errno.OK
                        }
                        AUDIO_FORMAT_PCM_8_BIT -> AndroidEnginePostprocessor { outLeft, outRight, buffer ->
                            val buf = buffer.pointed.u8!!
                            for (i in outLeft.indices) {
                                buf[i * 2] = (outLeft[i] * Byte.MAX_VALUE + 128).toInt().toUByte()
                                buf[i * 2 + 1] = (outRight[i] * Byte.MAX_VALUE + 128).toInt().toUByte()
                            }
                            Errno.OK
                        }
                        else -> return Errno.EINVAL.also { engine.setProperty(EngineProperty.GlobalEnabled, false) }
                    }
                } else {
                    when (config.outputCfg.format) {
                        else -> return Errno.EINVAL.also { engine.setProperty(EngineProperty.GlobalEnabled, false) }
                    }
                }

            }

            EFFECT_CMD_RESET -> {
                if (replySize != sizeOf<int32_tVar>() || pReplyData == null) {
                    logger.error("EFFECT_CMD_RESET called with invalid replySize = $replySize, pReplyData = $pReplyData, expected replySize = ${sizeOf<int32_tVar>()}")
                    return Errno.EINVAL
                }

                engine.reset()

                pReplyData.reinterpret<int32_tVar>().pointed.value = Errno.OK.errCode
            }

            EFFECT_CMD_ENABLE -> {
                if (replySize != sizeOf<int32_tVar>() || pReplyData == null) {
                    logger.error("EFFECT_CMD_ENABLE called with invalid replySize = $replySize, pReplyData = $pReplyData, expected replySize = ${sizeOf<int32_tVar>()}")
                    return Errno.EINVAL
                }

                pReplyData.reinterpret<int32_tVar>().pointed.value = engine.setProperty(EngineProperty.GlobalEnabled, true).errCode
            }

            EFFECT_CMD_DISABLE -> {
                if (replySize != sizeOf<int32_tVar>() || pReplyData == null) {
                    logger.error("EFFECT_CMD_DISABLE called with invalid replySize = $replySize, pReplyData = $pReplyData, expected replySize = ${sizeOf<int32_tVar>()}")
                    return Errno.EINVAL
                }

                pReplyData.reinterpret<int32_tVar>().pointed.value = engine.setProperty(EngineProperty.GlobalEnabled, false).errCode
            }

            EFFECT_CMD_SET_PARAM -> {
                // TODO
            }

            EFFECT_CMD_GET_PARAM -> {
                // TODO
            }

            EFFECT_CMD_GET_CONFIG -> {
                if (replySize != sizeOf<effect_config_t>() || pReplyData == null) {
                    logger.error("EFFECT_CMD_GET_CONFIG called with invalid replySize = $replySize, pReplyData = $pReplyData, expected replySize = ${sizeOf<effect_config_t>()}")
                    return Errno.EINVAL
                }

                memcpy(pReplyData.reinterpret<effect_config_t>(), config.ptr, sizeOf<effect_config_t>().convert())
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
