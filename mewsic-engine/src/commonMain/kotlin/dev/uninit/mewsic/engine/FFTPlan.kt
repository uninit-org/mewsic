package dev.uninit.mewsic.engine

import dev.uninit.mewsic.engine.ext.reverseBits
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class FFTPlan(private val size: Int) {
    private val levels = 31 - size.countLeadingZeroBits()

    private val cosLookup = FloatArray(size / 2) {
        cos(2 * PI * it / size).toFloat()
    }
    private val sinLookup = FloatArray(size / 2) {
        sin(2 * PI * it / size).toFloat()
    }

    init {
        require(1 shl levels == size) { "Size must be a power of 2" }
    }

    fun executeInverse(real: FloatArray, imag: FloatArray) {
        execute(imag, real)
    }

    fun execute(signal: FloatArray): Pair<FloatArray, FloatArray> {
        val real = signal.copyOf()
        val imag = FloatArray(size)
        execute(real, imag)
        return real to imag
    }

    fun execute(real: FloatArray, imag: FloatArray) {
        require(real.size == size && imag.size == size)

        for (i in real.indices) {
            val j = i.reverseBits() ushr 32 - levels
            if (j > i) {
                var temp = real[i]
                real[i] = real[j]
                real[j] = temp
                temp = imag[i]
                imag[i] = imag[j]
                imag[j] = temp
            }
        }

        var size = 2
        while (size <= real.size) {
            val halfSize = size / 2
            val step = real.size / size
            var i = 0
            while (i < real.size) {
                var j = i
                var k = 0
                while (j < i + halfSize) {
                    val l = j + halfSize
                    val tmpReal = real[l] * cosLookup[k] + imag[l] * sinLookup[k]
                    val tmpImag = -real[l] * sinLookup[k] + imag[l] * cosLookup[k]
                    real[l] = real[j] - tmpReal
                    imag[l] = imag[j] - tmpImag
                    real[j] += tmpReal
                    imag[j] += tmpImag
                    j++
                    k += step
                }
                i += size
            }
            if (size == real.size) {
                break
            }
            size *= 2
        }
    }
}
