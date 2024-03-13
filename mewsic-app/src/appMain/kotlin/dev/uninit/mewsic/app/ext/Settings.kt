package dev.uninit.mewsic.app.ext

import com.russhwolf.settings.Settings
import dev.uninit.mewsic.app.setting.BoundSetting
import dev.uninit.mewsic.app.setting.SettingKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


inline fun <reified T> Settings.bound() = BoundSetting<T>(this, null)
inline fun <reified T> Settings.bound(default: T) = BoundSetting<T>(this, null, default)
inline fun <reified T> Settings.bound(key: String) = BoundSetting<T>(this, key)
inline fun <reified T> Settings.bound(key: String, default: T) = BoundSetting<T>(this, key, default)

inline operator fun <reified T: Any> Settings.get(key: String): T? {
    return this.getStringOrNull(key)?.let(Json.Default::decodeFromString)
}

inline operator fun <reified T: Any> Settings.get(key: String, defaultValue: T): T {
    return this.getStringOrNull(key)?.let(Json.Default::decodeFromString) ?: defaultValue
}

inline operator fun <reified T> Settings.get(key: SettingKey<T>): T? {
    return this.getStringOrNull(key.keyString)?.let(Json.Default::decodeFromString)
}

inline operator fun <reified T> Settings.get(key: SettingKey<T>, defaultValue: T): T {
    return this.getStringOrNull(key.keyString)?.let(Json.Default::decodeFromString) ?: defaultValue
}

inline operator fun <reified T> Settings.set(key: SettingKey<T>, value: T) {
    this.putString(key.keyString, Json.encodeToString(value))
}

fun <T> Settings.remove(key: SettingKey<T>) {
    this.remove(key.keyString)
}
