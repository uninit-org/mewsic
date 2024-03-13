package dev.uninit.mewsic.engine.linux

import dev.uninit.mewsic.engine.Engine
import gst.GstME
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
internal val CPointer<GstME>?.engineKt: Engine
    get() = this!!.pointed.engine!!.asStableRef<Engine>().get()

@OptIn(ExperimentalForeignApi::class)
internal val CPointer<GstME>?.dbusKt: DBusHandler
    get() = this!!.pointed.dbus!!.asStableRef<DBusHandler>().get()

@OptIn(ExperimentalForeignApi::class)
internal fun CArrayPointer<ByteVar>.assign(str: String) {
    str.encodeToByteArray().forEachIndexed { index, byte ->
        this[index] = byte
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun NativeFreeablePlacement.alloc(value: String): CPointer<ByteVar> {
    val ptr = allocArray<ByteVar>(value.length + 1)
    ptr.assign(value)
    ptr[value.length] = 0
    return ptr
}
