package dev.uninit.mewsic.engine.unit

import kotlin.jvm.JvmInline
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@JvmInline
value class Angle(val radians: Float) {
    val degrees: Float
        get() = radians * 180.0f / PI.toFloat()

    operator fun plus(other: Angle): Angle {
        return Angle(radians + other.radians % (2.0f * PI.toFloat()))
    }

    operator fun minus(other: Angle): Angle {
        return Angle(radians - other.radians % (2.0f * PI.toFloat()))
    }

    operator fun times(factor: Float): Angle {
        return Angle(radians * factor)
    }

    operator fun div(factor: Float): Angle {
        return Angle(radians / factor)
    }

    fun sin(): Float {
        return sin(radians)
    }

    fun cos(): Float {
        return cos(radians)
    }

    companion object {
        fun fromDegrees(degrees: Float): Angle {
            return Angle(degrees * PI.toFloat() / 180.0f)
        }
    }
}
