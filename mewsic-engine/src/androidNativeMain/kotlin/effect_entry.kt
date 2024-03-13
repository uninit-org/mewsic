import android.audio.*
import dev.uninit.mewsic.engine.Engine
import dev.uninit.mewsic.engine.Errno
import dev.uninit.mewsic.engine.android.*
import kotlinx.cinterop.*
import platform.posix.int32_t
import platform.posix.memcmp
import platform.posix.memcpy
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal val descriptor = nativeHeap.alloc<effect_descriptor_t> {
    memcpy(type.ptr, EFFECT_UUID_NULL, sizeOf<effect_uuid_t>().convert())
    memcpy(uuid.ptr, EFFECT_UUID_CUSTOM.ptr, sizeOf<effect_uuid_t>().convert())

    apiVersion = 0x00020000u
    flags = (EFFECT_FLAG_TYPE_INSERT or EFFECT_FLAG_INSERT_FIRST or EFFECT_FLAG_INSERT_LAST or EFFECT_FLAG_VOLUME_CTRL or EFFECT_FLAG_DEVICE_IND).toUInt()
    cpuLoad = 8u
    memoryUsage = 1u
    name.assign("Mewsic Engine")
    implementor.assign("Mewsic")
}

@Suppress("UNUSED", "FunctionName")
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class, ExperimentalNativeApi::class)
@CName("MECreateEffect")
fun CreateEffect(uuid: CPointer<effect_uuid_t>?, sessionId: int32_t, ioId: int32_t, pHandle: CPointerVarOf<effect_handle_t>?): int32_t {
    if (uuid == null || pHandle == null) {
        return -Errno.EINVAL.errCode
    }

    if (memcmp(uuid, descriptor.uuid.ptr, sizeOf<effect_uuid_t>().convert()) != 0) {
        return -Errno.ENOENT.errCode
    }

    val itfe = nativeHeap.alloc<effect_interface_s> {
        process = intfProcess
        command = intfCommand
        get_descriptor = intfGetDescriptor
        process_reverse = null
    }

    nativeHeap.alloc<CustomEffectHandle> {
        this.itfe = itfe.ptr
        engine = StableRef.create(Engine()).asCPointer()
    }

    return Errno.OK.errCode
}

@Suppress("UNUSED", "FunctionName")
@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
@CName("MEReleaseEffect")
fun ReleaseEffect(handle: effect_handle_t?): int32_t {
    if (handle == null) {
        return -Errno.EINVAL.errCode
    }

    val cHandle = handle.reinterpret<CustomEffectHandle>()

    // Dispose engine
    cHandle.pointed.engineKt.dispose()
    cHandle.pointed.engine!!.asStableRef<Engine>().dispose()

    // Dispose interface
    cHandle.pointed.itfe?.let(nativeHeap::free)

    // Dispose handle
    nativeHeap.free(cHandle.rawValue)

    return Errno.OK.errCode
}

@Suppress("UNUSED", "FunctionName")
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class, ExperimentalNativeApi::class)
@CName("MEGetDescriptor")
fun GetDescriptor(uuid: CPointer<effect_uuid_t>?, pDescriptor: CPointer<effect_descriptor_t>?): int32_t {
    if (uuid == null || pDescriptor == null) {
        return -Errno.EINVAL.errCode
    }

    if (memcmp(uuid, descriptor.type.ptr, sizeOf<effect_uuid_t>().convert()) != 0) {
        return -Errno.ENOENT.errCode
    }

    memcpy(pDescriptor, descriptor.ptr, sizeOf<effect_descriptor_t>().convert())
    return Errno.OK.errCode
}
