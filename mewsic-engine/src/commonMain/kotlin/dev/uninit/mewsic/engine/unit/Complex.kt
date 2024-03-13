package dev.uninit.mewsic.engine.unit

import dev.uninit.mewsic.engine.ext.radians
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class Complex(
    val real: Float,
    val imag: Float
) {
    val angle: Angle
        get() = atan2(imag, real).radians

    val magnitude: Float
        get() = sqrt(real * real + imag * imag)

    operator fun plus(other: Complex): Complex {
        return Complex(real + other.real, imag + other.imag)
    }

    operator fun minus(other: Complex): Complex {
        return Complex(real - other.real, imag - other.imag)
    }

    operator fun times(factor: Float): Complex {
        return Complex(real * factor, imag * factor)
    }

    operator fun div(factor: Float): Complex {
        return Complex(real / factor, imag / factor)
    }

    operator fun times(other: Complex): Complex {
        return Complex(
            real * other.real - imag * other.imag,
            real * other.imag + imag * other.real
        )
    }

    operator fun div(other: Complex): Complex {
        val denominator = other.real * other.real + other.imag * other.imag
        return Complex(
            (real * other.real + imag * other.imag) / denominator,
            (imag * other.real - real * other.imag) / denominator
        )
    }

    fun pow(exponent: Float): Complex {
        val magnitude = magnitude.pow(exponent)
        val angle = angle * exponent
        return fromPolar(magnitude, angle)
    }

    companion object {
        fun fromPolar(magnitude: Float, angle: Angle): Complex {
            return Complex(magnitude * angle.cos(), magnitude * angle.sin())
        }
    }
}
