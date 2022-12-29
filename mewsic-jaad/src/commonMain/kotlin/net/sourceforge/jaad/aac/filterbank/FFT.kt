package net.sourceforge.jaad.aac.filterbank

import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.filterbank.FFTTables.Companion.FFT_TABLE_480
import net.sourceforge.jaad.aac.filterbank.FFTTables.Companion.FFT_TABLE_512
import net.sourceforge.jaad.aac.filterbank.FFTTables.Companion.FFT_TABLE_60
import net.sourceforge.jaad.aac.filterbank.FFTTables.Companion.FFT_TABLE_64

internal class FFT(private val length: Int) : FFTTables {
    private val roots: Array<FloatArray> = when (length) {
        64 -> FFT_TABLE_64
        512 -> FFT_TABLE_512
        60 -> FFT_TABLE_60
        480 -> FFT_TABLE_480
        else -> throw AACException("unexpected FFT length: $length")
    }
    private val rev: Array<FloatArray> = Array(length) { FloatArray(2) }
    private val a: FloatArray = FloatArray(2)
    private val b: FloatArray = FloatArray(2)
    private val c: FloatArray = FloatArray(2)
    private val d: FloatArray = FloatArray(2)
    private val e1: FloatArray = FloatArray(2)
    private val e2: FloatArray = FloatArray(2)

    fun process(`in`: Array<FloatArray>, forward: Boolean) {
        val imOff = if (forward) 2 else 1
        val scale = if (forward) length else 1
        //bit-reversal
        var ii = 0
        for (i in 0 until length) {
            rev[i][0] = `in`[ii][0]
            rev[i][1] = `in`[ii][1]
            var k = length shr 1
            while (k in 1..ii) {
                ii -= k
                k = k shr 1
            }
            ii += k
        }
        for (i in 0 until length) {
            `in`[i][0] = rev[i][0]
            `in`[i][1] = rev[i][1]
        }

        //bottom base-4 round
        run {
            var i = 0
            while (i < length) {
                a[0] = `in`[i][0] + `in`[i + 1][0]
                a[1] = `in`[i][1] + `in`[i + 1][1]
                b[0] = `in`[i + 2][0] + `in`[i + 3][0]
                b[1] = `in`[i + 2][1] + `in`[i + 3][1]
                c[0] = `in`[i][0] - `in`[i + 1][0]
                c[1] = `in`[i][1] - `in`[i + 1][1]
                d[0] = `in`[i + 2][0] - `in`[i + 3][0]
                d[1] = `in`[i + 2][1] - `in`[i + 3][1]
                `in`[i][0] = a[0] + b[0]
                `in`[i][1] = a[1] + b[1]
                `in`[i + 2][0] = a[0] - b[0]
                `in`[i + 2][1] = a[1] - b[1]
                e1[0] = c[0] - d[1]
                e1[1] = c[1] + d[0]
                e2[0] = c[0] + d[1]
                e2[1] = c[1] - d[0]
                if (forward) {
                    `in`[i + 1][0] = e2[0]
                    `in`[i + 1][1] = e2[1]
                    `in`[i + 3][0] = e1[0]
                    `in`[i + 3][1] = e1[1]
                } else {
                    `in`[i + 1][0] = e1[0]
                    `in`[i + 1][1] = e1[1]
                    `in`[i + 3][0] = e2[0]
                    `in`[i + 3][1] = e2[1]
                }
                i += 4
            }
        }

        //iterations from bottom to top
        var shift: Int
        var m: Int
        var km: Int
        var rootRe: Float
        var rootIm: Float
        var zRe: Float
        var zIm: Float
        var i = 4
        while (i < length) {
            shift = i shl 1
            m = length / shift
            var j = 0
            while (j < length) {
                for (k in 0 until i) {
                    km = k * m
                    rootRe = roots[km][0]
                    rootIm = roots[km][imOff]
                    zRe = `in`[i + j + k][0] * rootRe - `in`[i + j + k][1] * rootIm
                    zIm = `in`[i + j + k][0] * rootIm + `in`[i + j + k][1] * rootRe
                    `in`[i + j + k][0] = (`in`[j + k][0] - zRe) * scale
                    `in`[i + j + k][1] = (`in`[j + k][1] - zIm) * scale
                    `in`[j + k][0] = (`in`[j + k][0] + zRe) * scale
                    `in`[j + k][1] = (`in`[j + k][1] + zIm) * scale
                }
                j += shift
            }
            i = i shl 1
        }
    }
}
