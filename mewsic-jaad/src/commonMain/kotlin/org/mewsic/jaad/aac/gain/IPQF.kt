package net.sourceforge.jaad.aac.gain

import net.sourceforge.jaad.aac.gain.GCConstants.Companion.BANDS
import net.sourceforge.jaad.aac.gain.GCConstants.Companion.NPQFTAPS
import net.sourceforge.jaad.aac.gain.PQFTables.Companion.COEFS_Q0
import net.sourceforge.jaad.aac.gain.PQFTables.Companion.COEFS_Q1
import net.sourceforge.jaad.aac.gain.PQFTables.Companion.COEFS_T0
import net.sourceforge.jaad.aac.gain.PQFTables.Companion.COEFS_T1

//inverse polyphase quadrature filter
internal class IPQF : GCConstants, PQFTables {
    private val buf: FloatArray
    private val tmp1: Array<FloatArray>
    private val tmp2: Array<FloatArray>

    init {
        buf = FloatArray(BANDS)
        tmp1 = Array(BANDS / 2) { FloatArray(NPQFTAPS / BANDS) }
        tmp2 = Array(BANDS / 2) { FloatArray(NPQFTAPS / BANDS) }
    }

    fun process(`in`: Array<FloatArray>, frameLen: Int, maxBand: Int, out: FloatArray) {
        var i: Int
        var j: Int
        i = 0
        while (i < frameLen) {
            out[i] = 0.0f
            i++
        }
        i = 0
        while (i < frameLen / BANDS) {
            j = 0
            while (j < BANDS) {
                buf[j] = `in`[j][i]
                j++
            }
            performSynthesis(buf, out, i * BANDS)
            i++
        }
    }

    private fun performSynthesis(`in`: FloatArray, out: FloatArray, outOff: Int) {
        val kk: Int = NPQFTAPS / (2 * BANDS)
        var i: Int
        var n: Int
        var k: Int
        var acc: Float
        n = 0
        while (n < BANDS / 2) {
            k = 0
            while (k < 2 * kk - 1) {
                tmp1[n][k] = tmp1[n][k + 1]
                tmp2[n][k] = tmp2[n][k + 1]
                ++k
            }
            ++n
        }
        n = 0
        while (n < BANDS / 2) {
            acc = 0.0f
            i = 0
            while (i < BANDS) {
                acc += COEFS_Q0.get(n).get(i) * `in`[i]
                ++i
            }
            tmp1[n][2 * kk - 1] = acc
            acc = 0.0f
            i = 0
            while (i < BANDS) {
                acc += COEFS_Q1.get(n).get(i) * `in`[i]
                ++i
            }
            tmp2[n][2 * kk - 1] = acc
            ++n
        }
        n = 0
        while (n < BANDS / 2) {
            acc = 0.0f
            k = 0
            while (k < kk) {
                acc += COEFS_T0.get(n).get(k) * tmp1[n][2 * kk - 1 - 2 * k]
                ++k
            }
            k = 0
            while (k < kk) {
                acc += COEFS_T1.get(n).get(k) * tmp2[n][2 * kk - 2 - 2 * k]
                ++k
            }
            out[outOff + n] = acc
            acc = 0.0f
            k = 0
            while (k < kk) {
                acc += COEFS_T0.get(BANDS - 1 - n).get(k) * tmp1[n][2 * kk - 1 - 2 * k]
                ++k
            }
            k = 0
            while (k < kk) {
                acc -= COEFS_T1.get(BANDS - 1 - n).get(k) * tmp2[n][2 * kk - 2 - 2 * k]
                ++k
            }
            out[outOff + BANDS - 1 - n] = acc
            ++n
        }
    }
}
