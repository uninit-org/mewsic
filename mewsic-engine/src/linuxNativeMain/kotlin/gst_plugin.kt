import dev.uninit.buildconfig.EngineBuildConfig
import dev.uninit.mewsic.engine.linux.MEInit
import dev.uninit.mewsic.engine.linux.alloc
import gst.*
import kotlinx.cinterop.*
import kotlin.experimental.ExperimentalNativeApi

@Suppress("PrivatePropertyName")
@OptIn(ExperimentalForeignApi::class)
private val MEInitGst = staticCFunction<CPointer<GstPlugin>?, gboolean> {
    gst_element_register(
        it,
        "mewsic_engine",
        GST_RANK_NONE,
        gst_mewsic_engine_get_type(),
    )
}

@OptIn(ExperimentalForeignApi::class)
private val descriptor = nativeHeap.alloc<GstPluginDesc> {
    major_version = GST_VERSION_MAJOR
    minor_version = GST_VERSION_MINOR
    name = nativeHeap.alloc("mewsic_engine_gstreamer")
    description = nativeHeap.alloc("Mewsic Engine")
    plugin_init = MEInitGst
    version = nativeHeap.alloc(EngineBuildConfig.VERSION)
    license = nativeHeap.alloc("BSD-3-Clause")
    source = nativeHeap.alloc("mewsic-engine")
    `package` = nativeHeap.alloc("mewsic-engine-gstreamer")
    origin = nativeHeap.alloc("https://github.com/uninit-org/mewsicapp")
    release_datetime = __GST_PACKAGE_RELEASE_DATETIME?.reinterpret()
}

@Suppress("FunctionName", "unused")
@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
@CName("gst_plugin_mewsic_engine_get_desc")
fun MEGetDesc(): CPointer<GstPluginDesc> {
    return descriptor.ptr
}

@Suppress("FunctionName", "unused")
@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
@CName("gst_plugin_mewsic_engine_register")
fun MERegister() {
    gst_plugin_register_static(
        descriptor.major_version,
        descriptor.minor_version,
        descriptor.name?.toKString(),
        descriptor.description?.toKString(),
        descriptor.plugin_init,
        descriptor.version?.toKString(),
        descriptor.license?.toKString(),
        descriptor.source?.toKString(),
        descriptor.`package`?.toKString(),
        descriptor.origin?.toKString(),
    )
}

@Suppress("FunctionName", "unused")
@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
@CName("gst_mewsic_engine_init")
fun MEInitPublic(self: CPointer<GstME>?) {
    MEInit(self)
}
