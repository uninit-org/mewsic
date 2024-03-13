@file:Suppress("PrivatePropertyName")

package dev.uninit.mewsic.engine.linux

import dev.uninit.buildconfig.EngineBuildConfig
import dev.uninit.mewsic.engine.Engine
import dev.uninit.mewsic.engine.EngineProperty
import dev.uninit.mewsic.engine.Errno
import dev.uninit.mewsic.engine.linux.ext.asDBus
import dev.uninit.mewsic.engine.linux.ext.fromDBus
import dev.uninit.mewsic.engine.unit.Gain
import dev.uninit.mewsic.utils.platform.Logger
import gio.*
import kotlinx.cinterop.*

private val dbusLogger = Logger.withPrefix("[d.u.m.e.l.DbusKt]")

@OptIn(ExperimentalForeignApi::class)
fun Errno.dbus() : CPointer<GError>? {
    return when(this) {
        Errno.OK -> null
        Errno.EINVAL -> g_error_new_literal(
            G_IO_ERROR,
            G_IO_ERROR_INVALID_ARGUMENT.convert(),
            "Invalid argument",
        )
        Errno.ENOMEM -> g_error_new_literal(
            G_IO_ERROR,
            G_IO_ERROR_NO_SPACE.convert(),
            "Out of memory",
        )
        Errno.EPERM -> g_error_new_literal(
            G_IO_ERROR,
            G_IO_ERROR_PERMISSION_DENIED.convert(),
            "Permission denied (not super user)",
        )
        Errno.EACCES -> g_error_new_literal(
            G_IO_ERROR,
            G_IO_ERROR_PERMISSION_DENIED.convert(),
            "Permission denied",
        )
        Errno.EBUSY -> g_error_new_literal(
            G_IO_ERROR,
            G_IO_ERROR_BUSY.convert(),
            "Device or resource busy",
        )
        else -> g_error_new_literal(
            G_IO_ERROR,
            G_IO_ERROR_FAILED.convert(),
            "Error: $this - No further information",
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
internal class DBusHandler(private val engine: Engine) {
    val ref = StableRef.create(this)
    private var ownerId: UInt = 0u
    var introspectionData: CPointer<GDBusNodeInfo>? = null

    fun start() {
        val handle = nativeHeap.alloc<DBusHandleWrapper>()
        handle.handle = ref.asCPointer()

        dbusLogger.info("Introspection XML: \n$INTROSPECTION_XML")
        memScoped {
            val err = alloc<CPointerVar<GError>>()
            introspectionData = g_dbus_node_info_new_for_xml(INTROSPECTION_XML, err.ptr)
            if (introspectionData == null) {
                dbusLogger.error("Error creating introspection data: ${err.value!!.pointed.message?.toKString()}")
                return
            }
        }

        ownerId = g_bus_own_name(
            G_BUS_TYPE_SESSION,
            "dev.uninit.mewsic.engine",
            G_BUS_NAME_OWNER_FLAGS_NONE,
            DBusOnBusAcquired,
            DBusOnNameAcquired,
            DBusOnNameLost,
            handle.ptr,
            DBusOnFree,
        )
    }

    fun stop() {
        g_bus_unown_name(ownerId)
        g_dbus_node_info_unref(introspectionData)
    }

    @Suppress("UNCHECKED_CAST")
    fun setProperty(name: String, value: CPointer<GVariant>): CPointer<GError>? {
        val item = EngineProperty.fromDBus(name.uppercase())

        return memScoped {
            val err = when (item) {
                EngineProperty.GlobalEnabled, EngineProperty.GainEnabled -> {
                    val enabled = g_variant_get_boolean(value)
                    engine.setProperty(item as EngineProperty<Boolean>, enabled == 1)
                }

                EngineProperty.GainValue -> {
                    val gain = Gain.fromAmplitude(g_variant_get_double(value).toFloat())
                    engine.setProperty(item as EngineProperty<Gain>, gain)
                }

                else -> {
                    return@memScoped g_error_new_literal(
                        G_IO_ERROR,
                        G_IO_ERROR_NOT_SUPPORTED.convert(),
                        "Property '$name' unknown",
                    )
                }
            }

            if (err != Errno.OK) {
                dbusLogger.error("Error setting property '$name': $err")
                return@memScoped err.dbus()
            }

            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getProperty(name: String): Pair<CPointer<GVariant>?, CPointer<GError>?> {
        val item = EngineProperty.fromDBus(name.uppercase())

        return memScoped {
            when (item) {
                EngineProperty.GlobalEnabled, EngineProperty.GainEnabled -> {
                    val value = engine.getProperty(item as EngineProperty<Boolean>)
                    g_variant_new_boolean(if (value) 1 else 0)
                }

                EngineProperty.GlobalSampleRate -> {
                    val value = engine.getProperty(item as EngineProperty<Int>)
                    g_variant_new_int32(value)
                }

                EngineProperty.GainValue -> {
                    val value = engine.getProperty(item as EngineProperty<Gain>)
                    g_variant_new_double(value.amplitude.toDouble())
                }

                else -> {
                    return@memScoped null to g_error_new_literal(
                        G_IO_ERROR,
                        G_IO_ERROR_NOT_SUPPORTED.convert(),
                        "Property '$name' unknown",
                    )
                }
            } to null
        }
    }

    fun dispose() {
        ref.dispose()
    }

    companion object {
        val INTROSPECTION_XML = """
<node>
    <interface name="dev.uninit.mewsic.engine.linux.MewsicDBusInterface">
        <method name="GetVersion">
            <arg type="s" name="version" direction="out"/>
        </method>
        <property name="${EngineProperty.GlobalSampleRate.asDBus()}" type="i" access="read"/>
        
        <property name="${EngineProperty.GlobalEnabled.asDBus()}" type="b" access="readwrite"/>
        <property name="${EngineProperty.GainEnabled.asDBus()}" type="b" access="readwrite"/>
        <property name="${EngineProperty.GainValue.asDBus()}" type="d" access="readwrite"/>
    </interface>
</node>
        """.trim()
    }
}

@OptIn(ExperimentalForeignApi::class)
private val DBusInterfaceMethodCall = staticCFunction<CPointer<GDBusConnection>?, CPointer<gcharVar>?, CPointer<gcharVar>?, CPointer<gcharVar>?, CPointer<gcharVar>?, CPointer<GVariant>?, CPointer<GDBusMethodInvocation>?, gpointer?, Unit> { connection, sender, objectPath, interfaceName, methodName, parameters, invocation, handle ->
    val target = methodName?.toKString() ?: return@staticCFunction

    dbusLogger.info("DBus method call: '$target'")

    when (target) {
        "GetVersion" -> {
            val str = g_variant_new("(s)", EngineBuildConfig.VERSION)
            g_dbus_method_invocation_return_value(invocation, str)
        }
        else -> {
            dbusLogger.error("Invalid method: $target")
            g_dbus_method_invocation_return_error_literal(
                invocation,
                G_IO_ERROR,
                G_IO_ERROR_NOT_SUPPORTED.convert(),
                "Method '$target' not supported",
            )
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private val DBusInterfaceGetProperty = staticCFunction<CPointer<GDBusConnection>?, CPointer<gcharVar>?, CPointer<gcharVar>?, CPointer<gcharVar>?, CPointer<gcharVar>?, CPointer<CPointerVar<GError>>?, gpointer?, CPointer<GVariant>?> { connection, sender, objectPath, interfaceName, propertyName, error, handle ->
    dbusLogger.info("DBus get_property: '${propertyName!!.toKString()}'")

    val ref = handle!!.reinterpret<DBusHandleWrapper>().pointed.handle!!.asStableRef<DBusHandler>()
    val dbus = ref.get()

    val (value, err) = dbus.getProperty(propertyName.toKString())

    if (err != null) {
        if (error != null) {
            error.pointed.value = err
        }
        return@staticCFunction null
    }

    value
}

@OptIn(ExperimentalForeignApi::class)
private val DBusInterfaceSetProperty = staticCFunction<CPointer<GDBusConnection>?, CPointer<gcharVar>?, CPointer<gcharVar>?, CPointer<gcharVar>?, CPointer<gcharVar>?, CPointer<GVariant>?, CPointer<CPointerVar<GError>>?, gpointer?, gboolean> { connection, sender, objectPath, interfaceName, propertyName, value, error, handle ->
    dbusLogger.info("DBus set_property: '${propertyName!!.toKString()}'")

    val ref = handle!!.reinterpret<DBusHandleWrapper>().pointed.handle!!.asStableRef<DBusHandler>()
    val dbus = ref.get()

    val err = dbus.setProperty(propertyName.toKString(), value!!)

    if (err != null) {
        error!!.pointed.value = err
        return@staticCFunction FALSE
    }

    TRUE
}

@OptIn(ExperimentalForeignApi::class)
private val dbusInterface = nativeHeap.alloc<GDBusInterfaceVTable>().also {
    it.method_call = DBusInterfaceMethodCall
    it.get_property = DBusInterfaceGetProperty
    it.set_property = DBusInterfaceSetProperty
}

@OptIn(ExperimentalForeignApi::class)
private val DBusOnBusAcquired = staticCFunction<CPointer<GDBusConnection>?, CArrayPointer<ByteVar>?, COpaquePointer?, Unit> { connection, name, handle ->
    val ref = handle!!.reinterpret<DBusHandleWrapper>().pointed.handle!!.asStableRef<DBusHandler>()
    val dbus = ref.get()

    val dbusIntfPtr = dbusInterface.ptr

    memScoped {
        val error = alloc<CPointerVar<GError>>()

        val dbusNodeIntf = g_dbus_node_info_lookup_interface(dbus.introspectionData, "dev.uninit.mewsic.engine.linux.MewsicDBusInterface")

        val regId = g_dbus_connection_register_object(
            connection,
            "/dev/uninit/mewsic/engine",
            dbusNodeIntf,
            dbusIntfPtr,
            handle,
            null,
            error.ptr,
        )

        if (regId <= 0u) {
            dbusLogger.error("Error registering DBus object: ${error.value?.pointed?.message?.toKString()}")
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private val DBusOnNameAcquired = staticCFunction<CPointer<GDBusConnection>?, CArrayPointer<ByteVar>?, COpaquePointer?, Unit> { connection, name, handle ->
    dbusLogger.info("DBus name acquired: ${name!!.toKString()}")
}

@OptIn(ExperimentalForeignApi::class)
private val DBusOnNameLost = staticCFunction<CPointer<GDBusConnection>?, CArrayPointer<ByteVar>?, COpaquePointer?, Unit> { connection, name, handle ->
    dbusLogger.info("DBus name lost")
}

@OptIn(ExperimentalForeignApi::class)
private val DBusOnFree = staticCFunction<COpaquePointer?, Unit> { handle ->
    val wrapper = handle!!.reinterpret<DBusHandleWrapper>()
    wrapper.pointed.handle!!.asStableRef<DBusHandler>().get().dispose()
    nativeHeap.free(handle)
}
