package dev.uninit.mewsic.engine

import dev.uninit.mewsic.engine.ext.dB
import dev.uninit.mewsic.engine.unit.Gain

sealed class EngineProperty<T>(default: T) {
    private var value: T = default

    data object GlobalEnabled : EngineProperty<Boolean>(false)
    data object GlobalSampleRate : EngineProperty<Int>(44100)

    data object GainEnabled : EngineProperty<Boolean>(false)
    data object GainValue : EngineProperty<Gain>(0.dB)

    operator fun getValue(thisRef: Any?, property: Any?): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: Any?, value: T) {
        this.value = value
    }

    companion object
}
