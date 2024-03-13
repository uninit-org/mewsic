package dev.uninit.mewsic.engine.android

import android.audio.audio_buffer_t
import android.audio.effect_command_e
import android.audio.effect_descriptor_t
import android.audio.effect_handle_t
import descriptor
import dev.uninit.mewsic.engine.Errno
import kotlinx.cinterop.*
import platform.posix.int32_t
import platform.posix.memcpy
import platform.posix.uint32_t
import platform.posix.uint32_tVar

@OptIn(ExperimentalForeignApi::class)
internal val intfCommand = staticCFunction<effect_handle_t?, effect_command_e, uint32_t, COpaquePointer?, CPointer<uint32_tVar>?, COpaquePointer?, int32_t> { self, cmdCode, cmdSize, pCmdData, replySize, pReplyData ->
    if (self == null) {
        return@staticCFunction -Errno.EINVAL.errCode
    }

    val instance = self.engineKt

    -instance.command(cmdCode, cmdSize, pCmdData, replySize, pReplyData).errCode
}

@OptIn(ExperimentalForeignApi::class)
internal val intfProcess = staticCFunction<effect_handle_t?, CPointer<audio_buffer_t>?, CPointer<audio_buffer_t>?, int32_t> { self, inBuffer, outBuffer ->
    if (self == null) {
        return@staticCFunction -Errno.EINVAL.errCode
    }

    val instance = self.engineKt

    -instance.process(inBuffer, outBuffer).errCode
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal val intfGetDescriptor = staticCFunction<effect_handle_t?, CPointer<effect_descriptor_t>?, int32_t> { self, pDescriptor ->
    if (pDescriptor == null) {
        return@staticCFunction -Errno.EINVAL.errCode
    }

    memcpy(pDescriptor, descriptor.ptr, sizeOf<effect_descriptor_t>().convert())
    0
}
