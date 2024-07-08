package dev.uninit.mewsic.app.setting

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import dev.uninit.mewsic.utils.weakmap.WeakValuesMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
class BoundSetting<T>(private val settings: Settings, private val key: String?, private val default: T?, private val type: KType, private val serializer: KSerializer<T>) : MutableState<T> {
    private var didInit = false
    private lateinit var _backed: MutableState<T?>

    override var value: T
        get() = _backed.value ?: default as T
        set(value) {
            _backed.value = value
        }

    override fun component1(): T {
        return value
    }

    override fun component2(): (T) -> Unit {
        return { value = it }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!didInit) {
            _backed = CACHE.getOrPut(key ?: property.name) {
                val loadedValue = when (type) {
                    String::class -> settings.getStringOrNull(key ?: property.name)
                    Int::class -> settings.getIntOrNull(key ?: property.name)
                    Long::class -> settings.getLongOrNull(key ?: property.name)
                    Float::class -> settings.getFloatOrNull(key ?: property.name)
                    Double::class -> settings.getDoubleOrNull(key ?: property.name)
                    Boolean::class -> settings.getBooleanOrNull(key ?: property.name)
                    else -> settings.getStringOrNull(key ?: property.name)?.let { JSON.decodeFromString(serializer, it) }
                } as T?

                mutableStateOf(loadedValue ?: default as T)
            } as MutableState<T?>
            didInit = true
        }

        val currentValue = _backed.getValue(thisRef, property)

        return (currentValue ?: default) as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (!didInit) {
            _backed = CACHE.getOrPut(key ?: property.name) {
                mutableStateOf(default)
            } as MutableState<T?>
            didInit = true
        }

        _backed.setValue(thisRef, property, value)

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
        private var CACHE = WeakValuesMap<String, MutableState<*>>()
        private val JSON = Json {
            // For compat when settings change
            ignoreUnknownKeys = true
        }

        inline operator fun <reified T> invoke(settings: Settings, key: String?, default: T): BoundSetting<T> {
            return BoundSetting(settings, key, default, typeOf<T>(), serializer())
        }

        inline operator fun <reified T> invoke(settings: Settings, key: String?): BoundSetting<T?> {
            return BoundSetting(settings, key, null, typeOf<T>(), serializer())
        }
    }
}
