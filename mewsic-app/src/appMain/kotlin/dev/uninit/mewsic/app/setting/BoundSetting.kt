package dev.uninit.mewsic.app.setting

import com.russhwolf.settings.Settings
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
class BoundSetting<T>(private val settings: Settings, private val key: String?, private val default: T?, private val type: KType, private val serializer: KSerializer<T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val res = when (type) {
            String::class -> settings.getStringOrNull(key ?: property.name)
            Int::class -> settings.getIntOrNull(key ?: property.name)
            Long::class -> settings.getLongOrNull(key ?: property.name)
            Float::class -> settings.getFloatOrNull(key ?: property.name)
            Double::class -> settings.getDoubleOrNull(key ?: property.name)
            Boolean::class -> settings.getBooleanOrNull(key ?: property.name)
            else -> settings.getStringOrNull(key ?: property.name)?.let { Json.decodeFromString(serializer, it) }
        }
        return (res ?: default) as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value == null) {
            settings.remove(key ?: property.name)
            return
        }

        when (type) {
            String::class -> settings.putString(key ?: property.name, value as String)
            Int::class -> settings.putInt(key ?: property.name, value as Int)
            Long::class -> settings
            Float::class -> settings
            Double::class -> settings
            Boolean::class -> settings
            else -> settings.putString(key ?: property.name, Json.encodeToString(serializer, value))
        }
    }

    companion object {
        inline operator fun <reified T> invoke(settings: Settings, key: String?, default: T): BoundSetting<T> {
            return BoundSetting(settings, key, default, typeOf<T>(), serializer())
        }

        inline operator fun <reified T> invoke(settings: Settings, key: String?): BoundSetting<T?> {
            return BoundSetting(settings, key, null, typeOf<T>(), serializer())
        }
    }
}
