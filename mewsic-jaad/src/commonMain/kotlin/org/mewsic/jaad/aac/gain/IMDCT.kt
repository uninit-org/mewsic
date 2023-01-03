package net.sourceforge.jaad.aac.gain

import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.gain.GCConstants.Companion.BANDS
import net.sourceforge.jaad.aac.gain.IMDCTTables.Companion.IMDCT_POST_TABLE_256
import net.sourceforge.jaad.aac.gain.IMDCTTables.Companion.IMDCT_POST_TABLE_32
import net.sourceforge.jaad.aac.gain.IMDCTTables.Companion.IMDCT_TABLE_256
import net.sourceforge.jaad.aac.gain.IMDCTTables.Companion.IMDCT_TABLE_32
import net.sourceforge.jaad.aac.gain.Windows.Companion.KBD_256
import net.sourceforge.jaad.aac.gain.Windows.Companion.KBD_32
import net.sourceforge.jaad.aac.gain.Windows.Companion.SINE_256
import net.sourceforge.jaad.aac.gain.Windows.Companion.SINE_32
import net.sourceforge.jaad.aac.syntax.ICSInfo.WindowSequence

//inverse modified discrete cosine transform
internal class IMDCT(private val frameLen: Int) : GCConstants,
    IMDCTTables, Windows {
    private val shortFrameLen: Int = frameLen / 8
    private val lbLong: Int = frameLen / BANDS
    private val lbShort: Int = shortFrameLen / BANDS
    private val lbMid: Int = (lbLong - lbShort) / 2

    @Throws(AACException::class)
    fun process(`in`: FloatArray, out: FloatArray, winShape: Int, winShapePrev: Int, winSeq: WindowSequence) {
        val buf = FloatArray(frameLen)
        var b: Int
        var j: Int
        var i: Int
        if (winSeq == WindowSequence.EIGHT_SHORT_SEQUENCE) {
            b = 0
            while (b < BANDS) {
                j = 0
                while (j < 8) {
                    i = 0
                    while (i < lbShort) {
                        if (b % 2 == 0) buf[lbLong * b + lbShort * j + i] =
                            `in`[shortFrameLen * j + lbShort * b + i] else buf[lbLong * b + lbShort * j + i] =
                            `in`[shortFrameLen * j + lbShort * b + lbShort - 1 - i]
                        i++
                    }
                    j++
                }
                b++
            }
        } else {
            b = 0
            while (b < BANDS) {
                i = 0
                while (i < lbLong) {
                    if (b % 2 == 0) buf[lbLong * b + i] = `in`[lbLong * b + i] else buf[lbLong * b + i] =
                        `in`[lbLong * b + lbLong - 1 - i]
                    i++
                }
                b++
            }
        }
        b = 0
        while (b < BANDS) {
            process2(buf, out, winSeq, winShape, winShapePrev, b)
            b++
        }
    }

    @Throws(AACException::class)
    private fun process2(
        `in`: FloatArray,
        out: FloatArray,
        winSeq: WindowSequence,
        winShape: Int,
        winShapePrev: Int,
        band: Int
    ) {
        val bufIn = FloatArray(lbLong)
        val bufOut = FloatArray(lbLong * 2)
        val window = FloatArray(lbLong * 2)
        val window1 = FloatArray(lbShort * 2)
        val window2 = FloatArray(lbShort * 2)

        //init windows
        var i: Int
        when (winSeq) {
            WindowSequence.ONLY_LONG_SEQUENCE -> {
                i = 0
                while (i < lbLong) {
                    window[i] = LONG_WINDOWS[winShapePrev][i]
                    window[lbLong * 2 - 1 - i] = LONG_WINDOWS[winShape][i]
                    i++
                }
            }

            WindowSequence.EIGHT_SHORT_SEQUENCE -> {
                i = 0
                while (i < lbShort) {
                    window1[i] = SHORT_WINDOWS[winShapePrev][i]
                    window1[lbShort * 2 - 1 - i] = SHORT_WINDOWS[winShape][i]
                    window2[i] = SHORT_WINDOWS[winShape][i]
                    window2[lbShort * 2 - 1 - i] = SHORT_WINDOWS[winShape][i]
                    i++
                }
            }

            WindowSequence.LONG_START_SEQUENCE -> {
                i = 0
                while (i < lbLong) {
                    window[i] = LONG_WINDOWS[winShapePrev][i]
                    i++
                }
                i = 0
                while (i < lbMid) {
                    window[i + lbLong] = 1.0f
                    i++
                }
                i = 0
                while (i < lbShort) {
                    window[i + lbMid + lbLong] = SHORT_WINDOWS[winShape][lbShort - 1 - i]
                    i++
                }
                i = 0
                while (i < lbMid) {
                    window[i + lbMid + lbLong + lbShort] = 0.0f
                    i++
                }
            }

            WindowSequence.LONG_STOP_SEQUENCE -> {
                i = 0
                while (i < lbMid) {
                    window[i] = 0.0f
                    i++
                }
                i = 0
                while (i < lbShort) {
                    window[i + lbMid] = SHORT_WINDOWS[winShapePrev][i]
                    i++
                }
                i = 0
                while (i < lbMid) {
                    window[i + lbMid + lbShort] = 1.0f
                    i++
                }
                i = 0
                while (i < lbLong) {
                    window[i + lbMid + lbShort + lbMid] = LONG_WINDOWS[winShape][lbLong - 1 - i]
                    i++
                }
            }
        }
        var j: Int
        if (winSeq == WindowSequence.EIGHT_SHORT_SEQUENCE) {
            var k: Int
            j = 0
            while (j < 8) {
                k = 0
                while (k < lbShort) {
                    bufIn[k] = `in`[band * lbLong + j * lbShort + k]
                    k++
                }

                if (j == 0) window1.copyInto(
                    window,
                    0,
                    0,
                    lbShort * 2
                ) else window2.copyInto(window, 0, 0, lbShort * 2)
                imdct(bufIn, bufOut, window, lbShort)
                k = 0
                while (k < lbShort * 2) {
                    out[band * lbLong * 2 + j * lbShort * 2 + k] = bufOut[k] / 32.0f
                    k++
                }
                j++
            }
        } else {
            j = 0
            while (j < lbLong) {
                bufIn[j] = `in`[band * lbLong + j]
                j++
            }
            imdct(bufIn, bufOut, window, lbLong)
            j = 0
            while (j < lbLong * 2) {
                out[band * lbLong * 2 + j] = bufOut[j] / 256.0f
                j++
            }
        }
    }

    @Throws(AACException::class)
    private fun imdct(`in`: FloatArray, out: FloatArray, window: FloatArray, n: Int) {
        val n2 = n / 2
        val table: Array<FloatArray>
        val table2: Array<FloatArray>
        when (n) {
            256 -> {
                table = IMDCT_TABLE_256
                table2 = IMDCT_POST_TABLE_256
            }

            32 -> {
                table = IMDCT_TABLE_32
                table2 = IMDCT_POST_TABLE_32
            }

            else -> throw AACException("gain control: unexpected IMDCT length")
        }
        val tmp = FloatArray(n)
        var i = 0
        while (i < n2) {
            tmp[i] = `in`[2 * i]
            ++i
        }
        i = n2
        while (i < n) {
            tmp[i] = -`in`[2 * n - 1 - 2 * i]
            ++i
        }

        //pre-twiddle
        val buf = Array(n2) { FloatArray(2) }
        i = 0
        while (i < n2) {
            buf[i][0] = table[i][0] * tmp[2 * i] - table[i][1] * tmp[2 * i + 1]
            buf[i][1] = table[i][0] * tmp[2 * i + 1] + table[i][1] * tmp[2 * i]
            i++
        }

        //fft
        FFT.process(buf, n2)

        //post-twiddle and reordering
        i = 0
        while (i < n2) {
            tmp[i] =
                table2[i][0] * buf[i][0] + table2[i][1] * buf[n2 - 1 - i][0] + table2[i][2] * buf[i][1] + table2[i][3] * buf[n2 - 1 - i][1]
            tmp[n - 1 - i] =
                table2[i][2] * buf[i][0] - table2[i][3] * buf[n2 - 1 - i][0] - table2[i][0] * buf[i][1] + table2[i][1] * buf[n2 - 1 - i][1]
            i++
        }

        //copy to output and apply window
        tmp.copyInto(out, n2, 0, n2)
        i = n2
        while (i < n * 3 / 2) {
            out[i] = -tmp[n * 3 / 2 - 1 - i]
            ++i
        }
        i = n * 3 / 2
        while (i < n * 2) {
            out[i] = -tmp[i - n * 3 / 2]
            ++i
        }
        i = 0
        while (i < n) {
            out[i] *= window[i]
            i++
        }
    }

    companion object {
        private val LONG_WINDOWS = arrayOf(SINE_256, KBD_256)
        private val SHORT_WINDOWS = arrayOf(SINE_32, KBD_32)
    }
}
