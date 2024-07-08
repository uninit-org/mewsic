package dev.uninit.mewsic.engine.android

import android.audio.audio_buffer_t
import dev.uninit.mewsic.engine.Errno
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal fun interface AndroidEnginePreprocessor {
    fun process(inBuffer: CPointer<audio_buffer_t>, left: FloatArray, right: FloatArray): Errno
}

@OptIn(ExperimentalForeignApi::class)
internal fun interface AndroidEnginePostprocessor {
    fun process(left: FloatArray, right: FloatArray, outBuffer: CPointer<audio_buffer_t>): Errno
}
