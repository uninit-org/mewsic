package dev.uninit.mewsic.engine.linux

import dev.uninit.mewsic.engine.Engine
import dev.uninit.mewsic.engine.EngineProperty
import dev.uninit.mewsic.engine.Errno
import dev.uninit.mewsic.utils.platform.Logger
import gst.*
import kotlinx.cinterop.*
import kotlin.experimental.ExperimentalNativeApi

private val gstLogger = Logger.withPrefix("[d.u.m.e.l.GstreamerKt]")

@Suppress("unused", "FunctionName")
@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
@CName("gst_mewsic_engine_class_init")
fun MEInitClass(plugin: CPointer<GstPlugin>?): Int {
    try {
        val gobjectClass = plugin!!.reinterpret<GObjectClass>()
        val gstElementClass = plugin.reinterpret<GstElementClass>()
        val baseTransformClass = plugin.reinterpret<GstBaseTransformClass>()
        val audioSelfClass = plugin.reinterpret<GstAudioFilterClass>()

        gobjectClass.pointed.set_property = MESetProperty
        gobjectClass.pointed.get_property = MEGetProperty
        gobjectClass.pointed.finalize = MEFinalize

        // TODO: Register properties?
//    g_object_class_install_property(gobjectClass, 1, g_param_spec_boolean("enabled", "Enabled", "Enable effect", TRUE, G_PARAM_READWRITE or GST_PARAM_CONTROLLABLE))

        gst_element_class_set_static_metadata(
            gstElementClass,
            "Mewsic Engine",
            "Filter/Effect/Audio",
            "Mewsic Engine for GStreamer",
            "MewsicApp <martmists@gmail.com>"
        )

        val caps = gst_caps_from_string(
            """
            audio/x-raw,
            format = (string) F32LE,
            rate = (int) [ 44100, 48000 ],
            channels = (int) [ 1, 2 ],
            layout = (string) interleaved
            """.trimIndent().replace("\n", " ")
        )

        gst_audio_filter_class_add_pad_templates(
            g_type_check_class_cast(
                plugin.reinterpret(),
                gst_mewsic_engine_get_type()
            )?.reinterpret(), caps
        )
        gst_caps_unref(caps)

        audioSelfClass.pointed.setup = MESetup

        baseTransformClass.pointed.transform_ip = MEProcessInPlace
        baseTransformClass.pointed.transform_ip_on_passthrough = FALSE
        baseTransformClass.pointed.stop = MEStop
    } catch (e: Exception) {
        gstLogger.error("Error initializing Mewsic Engine Class")
        e.printStackTrace()
        return FALSE
    }

    return TRUE
}

@Suppress("FunctionName")
@OptIn(ExperimentalForeignApi::class)
internal fun MEInit(self: CPointer<GstME>?) {
    try {
        gst_base_transform_set_in_place(self!!.reinterpret(), TRUE)
        gst_base_transform_set_gap_aware(self.reinterpret(), TRUE)

        val engine = Engine()
        val dbus = DBusHandler(engine)
        val engineRef = StableRef.create(engine)
        self.pointed.engine = engineRef.asCPointer()
        self.pointed.dbus = dbus.ref.asCPointer()

        g_mutex_init(self.pointed.lock.ptr)
        dbus.start()
    } catch (e: Exception) {
        gstLogger.error("Error initializing Mewsic Engine")
        e.printStackTrace()
    }
}

@Suppress("PrivatePropertyName")
@OptIn(ExperimentalForeignApi::class)
private val MESetProperty = staticCFunction<CPointer<GObject>?, guint, CPointer<GValue>?, CPointer<GParamSpec>?, Unit> { obj, propId, value, paramSpec ->
    // TODO
}

@Suppress("PrivatePropertyName")
@OptIn(ExperimentalForeignApi::class)
private val MEGetProperty = staticCFunction<CPointer<GObject>?, guint, CPointer<GValue>?, CPointer<GParamSpec>?, Unit> { obj, propId, value, paramSpec ->
    // TODO
}

@Suppress("PrivatePropertyName")
@OptIn(ExperimentalForeignApi::class)
private val MESetup = staticCFunction<CPointer<GstAudioFilter>?, CPointer<GstAudioInfo>?, gboolean> { base, info ->
    try {
        val gstEngine =
            g_type_check_instance_cast(base?.reinterpret(), gst_mewsic_engine_get_type())?.reinterpret<GstME>()
        val engine = gstEngine.engineKt

        val samplerate = info?.pointed?.rate ?: base!!.pointed.info.rate

        gstEngine!!.pointed.channels = info?.pointed?.channels ?: base!!.pointed.info.channels

        engine.setProperty(EngineProperty.GlobalSampleRate, samplerate)
    } catch (e: Exception) {
        gstLogger.error("Error setting up Mewsic Engine")
        e.printStackTrace()
        return@staticCFunction FALSE
    }

    TRUE
}

@Suppress("PrivatePropertyName")
@OptIn(ExperimentalForeignApi::class)
private val MEFinalize = staticCFunction<CPointer<GObject>?, Unit> { obj ->
    try {
        val gstEngine =
            g_type_check_instance_cast(obj?.reinterpret(), gst_mewsic_engine_get_type())?.reinterpret<GstME>()
        val engine = gstEngine.engineKt

        engine.dispose()
        gstEngine!!.pointed.engine!!.asStableRef<Engine>().dispose()
        gstEngine.pointed.engine = null

        gstEngine.dbusKt.stop()
    } catch (e: Exception) {
        gstLogger.error("Error finalizing Mewsic Engine")
        e.printStackTrace()
    }
}

@Suppress("PrivatePropertyName")
@OptIn(ExperimentalForeignApi::class)
private val MEProcessInPlace = staticCFunction<CPointer<GstBaseTransform>?, CPointer<GstBuffer>?, GstFlowReturn> { filter, buffer ->
    try {
        val gstEngine = g_type_check_instance_cast(filter?.reinterpret(), gst_mewsic_engine_get_type())?.reinterpret<GstME>() ?: return@staticCFunction GST_FLOW_ERROR
        val engine = gstEngine.engineKt

        val timestamp = buffer?.pointed?.pts ?: return@staticCFunction GST_FLOW_ERROR
        val streamTime = gst_segment_to_stream_time(filter!!.pointed.segment.ptr, GST_FORMAT_TIME, timestamp)

        if (streamTime != GST_CLOCK_TIME_NONE) {
            gst_object_sync_values(filter.reinterpret(), streamTime)
        }

        if (buffer.reinterpret<GstMiniObject>().pointed.flags and GST_BUFFER_FLAG_GAP != 0u) {
            return@staticCFunction GST_FLOW_OK
        }

        if (!engine.getProperty(EngineProperty.GlobalEnabled)) {
            return@staticCFunction GST_FLOW_OK
        }

        try {
            g_mutex_lock(gstEngine.pointed.lock.ptr)

            memScoped {
                val map = alloc<GstMapInfo>()
                gst_buffer_map(buffer, map.ptr, GST_MAP_READ or GST_MAP_WRITE)

                var numSamples =
                    map.size.convert<Int>() / (filter.reinterpret<GstAudioFilter>().pointed.info.finfo!!.pointed.depth shr 3)

                val inLeft: FloatArray
                val inRight: FloatArray

                if (gstEngine.pointed.channels == 2) {
                    numSamples /= 2

                    inLeft = FloatArray(numSamples)
                    inRight = FloatArray(numSamples)

                    map.apply {
                        val ptr = data!!.reinterpret<FloatVar>()
                        for (i in 0 until numSamples) {
                            inLeft[i] = ptr[i * 2]
                            inRight[i] = ptr[i * 2 + 1]
                        }
                    }
                } else {
                    inLeft = FloatArray(numSamples)
                    inRight = FloatArray(numSamples)

                    map.apply {
                        val ptr = data!!.reinterpret<FloatVar>()
                        for (i in 0 until numSamples) {
                            inLeft[i] = ptr[i]
                            inRight[i] = ptr[i]
                        }
                    }
                }

                val outLeft = FloatArray(numSamples)
                val outRight = FloatArray(numSamples)

                val errno = engine.process(inLeft, inRight, outLeft, outRight)
                if (errno != Errno.OK) {
                    gstLogger.error("Error processing Mewsic Engine: $errno")
                    return@staticCFunction GST_FLOW_ERROR
                }

                if (gstEngine.pointed.channels == 2) {
                    map.apply {
                        for (i in 0 until numSamples) {
                            val ptr = this.data!!.reinterpret<FloatVar>()
                            ptr[i * 2] = outLeft[i]
                            ptr[i * 2 + 1] = outRight[i]
                        }
                    }
                } else {
                    map.apply {
                        for (i in 0 until numSamples) {
                            val ptr = this.data!!.reinterpret<FloatVar>()
                            ptr[i] = ((outLeft[i] + outRight[i]) / 2f)
                        }
                    }
                }
            }
        } finally {
            g_mutex_unlock(gstEngine.pointed.lock.ptr)
        }
    } catch (e: Exception) {
        gstLogger.error("Error processing Mewsic Engine")
        e.printStackTrace()
        return@staticCFunction GST_FLOW_ERROR
    }

    GST_FLOW_OK
}

@Suppress("PrivatePropertyName")
@OptIn(ExperimentalForeignApi::class)
private val MEStop = staticCFunction<CPointer<GstBaseTransform>?, gboolean> { filter ->
    try {
        val engine = g_type_check_instance_cast(
            filter?.reinterpret(),
            gst_mewsic_engine_get_type()
        )?.reinterpret<GstME>().engineKt
    } catch (e: Exception) {
        gstLogger.error("Error stopping Mewsic Engine")
        e.printStackTrace()
        return@staticCFunction FALSE
    }

    TRUE
}
