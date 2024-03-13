package dev.uninit.mewsic.engine.unit

import kotlin.jvm.JvmInline
import kotlin.math.log10
import kotlin.math.pow

@JvmInline
value class Gain(val decibels: Float) {
    val amplitude: Float
        get() = 10.0f.pow(decibels / 20.0f)

    companion object {
        fun fromAmplitude(amplitude: Float): Gain {
            return Gain(20.0f * log10(amplitude))
        }
    }
}

