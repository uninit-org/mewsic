package dev.uninit.mewsic.engine.linux

import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.interfaces.DBusInterface
import org.freedesktop.dbus.interfaces.Properties

interface MewsicDBusInterface : DBusInterface {
    @Suppress("FunctionName")
    fun GetVersion(): String

    @Suppress("FunctionName")
    class Instance(val props: Properties, val mewsic: MewsicDBusInterface) {
        fun <T> Get(propertyName: String) = props.Get<T>("com.mewsic.engine.platform.linux.MewsicDBusInterface", propertyName)
        fun <T> Set(propertyName: String, value: T) = props.Set("com.mewsic.engine.platform.linux.MewsicDBusInterface", propertyName, value)
        fun GetVersion() = mewsic.GetVersion()
    }

    companion object {
        fun on(connection: DBusConnection) = Instance(
            connection.getRemoteObject(
                "com.mewsic.engine",
                "/com/mewsic/engine",
                Properties::class.java
            ),
            connection.getRemoteObject(
                "com.mewsic.engine",
                "/com/mewsic/engine",
                MewsicDBusInterface::class.java
            )
        )
    }
}
