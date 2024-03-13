package dev.uninit.mewsic.engine.linux.ext

import dev.uninit.mewsic.engine.EngineProperty

fun EngineProperty<*>.asDBus(): String {
    return when (this) {
        EngineProperty.GlobalEnabled -> "GLOBAL_ENABLED"
        EngineProperty.GlobalSampleRate -> "GLOBAL_SAMPLE_RATE"
        EngineProperty.GainEnabled -> "GAIN_ENABLED"
        EngineProperty.GainValue -> "GAIN_VALUE"
        else -> {
            throw IllegalArgumentException("Unknown property: $this")
        }
    }
}

fun EngineProperty.Companion.fromDBus(name: String): EngineProperty<*> {
    return when (name) {
        "GLOBAL_ENABLED" -> EngineProperty.GlobalEnabled
        "GLOBAL_SAMPLE_RATE" -> EngineProperty.GlobalSampleRate
        "GAIN_ENABLED" -> EngineProperty.GainEnabled
        "GAIN_VALUE" -> EngineProperty.GainValue
        else -> throw IllegalArgumentException("Unknown property: $name")
    }
}
