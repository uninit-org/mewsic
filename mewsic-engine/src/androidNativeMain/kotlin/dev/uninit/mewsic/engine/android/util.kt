package dev.uninit.mewsic.engine.android

import android.audio.CustomEffectHandle
import android.audio.effect_handle_t
import kotlinx.cinterop.*


@OptIn(ExperimentalForeignApi::class)
internal inline val effect_handle_t.engineKt: AndroidEngineProxy
    get() = this.reinterpret<CustomEffectHandle>().pointed.engineKt


@OptIn(ExperimentalForeignApi::class)
internal inline val CustomEffectHandle.engineKt: AndroidEngineProxy
    get() = this.engine!!.asStableRef<AndroidEngineProxy>().get()

@OptIn(ExperimentalForeignApi::class)
internal fun CArrayPointer<ByteVar>.assign(str: String) {
    str.encodeToByteArray().forEachIndexed { index, byte ->
        this[index] = byte
    }
}
