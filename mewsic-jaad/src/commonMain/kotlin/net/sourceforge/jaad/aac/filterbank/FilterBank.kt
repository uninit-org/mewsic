package net.sourceforge.jaad.aac.filterbank

import net.sourceforge.jaad.aac.filterbank.KBDWindows.Companion.KBD_1024
import net.sourceforge.jaad.aac.filterbank.KBDWindows.Companion.KBD_120
import net.sourceforge.jaad.aac.filterbank.KBDWindows.Companion.KBD_128
import net.sourceforge.jaad.aac.filterbank.KBDWindows.Companion.KBD_960
import net.sourceforge.jaad.aac.filterbank.SineWindows.Companion.SINE_1024
import net.sourceforge.jaad.aac.filterbank.SineWindows.Companion.SINE_120
import net.sourceforge.jaad.aac.filterbank.SineWindows.Companion.SINE_128
import net.sourceforge.jaad.aac.filterbank.SineWindows.Companion.SINE_960
import net.sourceforge.jaad.aac.syntax.Constants
import net.sourceforge.jaad.aac.syntax.ICSInfo.WindowSequence

class FilterBank(smallFrames: Boolean, channels: Int) : Constants, SineWindows,
    KBDWindows {
    private val LONG_WINDOWS // = {SINE_LONG, KBD_LONG};
            : Array<FloatArray>
    private val SHORT_WINDOWS // = {SINE_SHORT, KBD_SHORT};
            : Array<FloatArray>
    private var length = 0
    private var shortLen = 0
    private val mid: Int
    private val trans: Int
    private val mdctShort: MDCT
    private val mdctLong: MDCT
    private val buf: FloatArray
    private val overlaps: Array<FloatArray>

    init {
        if (smallFrames) {
            length = Constants.WINDOW_SMALL_LEN_LONG
            shortLen = Constants.WINDOW_SMALL_LEN_SHORT
            LONG_WINDOWS = arrayOf(SINE_960, KBD_960)
            SHORT_WINDOWS = arrayOf(SINE_120, KBD_120)
        } else {
            length = Constants.WINDOW_LEN_LONG
            shortLen = Constants.WINDOW_LEN_SHORT
            LONG_WINDOWS = arrayOf(SINE_1024, KBD_1024)
            SHORT_WINDOWS = arrayOf(SINE_128, KBD_128)
        }
        mid = (length - shortLen) / 2
        trans = shortLen / 2
        mdctShort = MDCT(shortLen * 2)
        mdctLong = MDCT(length * 2)
        overlaps = Array(channels) { FloatArray(length) }
        buf = FloatArray(2 * length)
    }

    fun process(
        windowSequence: WindowSequence?,
        windowShape: Int,
        windowShapePrev: Int,
        `in`: FloatArray,
        out: FloatArray,
        channel: Int
    ) {
        var i: Int
        val overlap = overlaps[channel]
        when (windowSequence) {
            WindowSequence.ONLY_LONG_SEQUENCE -> {
                mdctLong.process(`in`, 0, buf, 0)
                //add second half output of previous frame to windowed output of current frame
                i = 0
                while (i < length) {
                    out[i] = overlap[i] + buf[i] * LONG_WINDOWS[windowShapePrev][i]
                    i++
                }

                //window the second half and save as overlap for next frame
                i = 0
                while (i < length) {
                    overlap[i] = buf[length + i] * LONG_WINDOWS[windowShape][length - 1 - i]
                    i++
                }
            }

            WindowSequence.LONG_START_SEQUENCE -> {
                mdctLong.process(`in`, 0, buf, 0)
                //add second half output of previous frame to windowed output of current frame
                i = 0
                while (i < length) {
                    out[i] = overlap[i] + buf[i] * LONG_WINDOWS[windowShapePrev][i]
                    i++
                }

                //window the second half and save as overlap for next frame
                i = 0
                while (i < mid) {
                    overlap[i] = buf[length + i]
                    i++
                }
                i = 0
                while (i < shortLen) {
                    overlap[mid + i] = buf[length + mid + i] * SHORT_WINDOWS[windowShape][shortLen - i - 1]
                    i++
                }
                i = 0
                while (i < mid) {
                    overlap[mid + shortLen + i] = 0f
                    i++
                }
            }

            WindowSequence.EIGHT_SHORT_SEQUENCE -> {
                i = 0
                while (i < 8) {
                    mdctShort.process(`in`, i * shortLen, buf, 2 * i * shortLen)
                    i++
                }

                //add second half output of previous frame to windowed output of current frame
                i = 0
                while (i < mid) {
                    out[i] = overlap[i]
                    i++
                }
                i = 0
                while (i < shortLen) {
                    out[mid + i] = overlap[mid + i] + buf[i] * SHORT_WINDOWS[windowShapePrev][i]
                    out[mid + 1 * shortLen + i] =
                        overlap[mid + shortLen * 1 + i] + buf[shortLen * 1 + i] * SHORT_WINDOWS[windowShape][shortLen - 1 - i] + buf[shortLen * 2 + i] * SHORT_WINDOWS[windowShape][i]
                    out[mid + 2 * shortLen + i] =
                        overlap[mid + shortLen * 2 + i] + buf[shortLen * 3 + i] * SHORT_WINDOWS[windowShape][shortLen - 1 - i] + buf[shortLen * 4 + i] * SHORT_WINDOWS[windowShape][i]
                    out[mid + 3 * shortLen + i] =
                        overlap[mid + shortLen * 3 + i] + buf[shortLen * 5 + i] * SHORT_WINDOWS[windowShape][shortLen - 1 - i] + buf[shortLen * 6 + i] * SHORT_WINDOWS[windowShape][i]
                    if (i < trans) out[mid + 4 * shortLen + i] =
                        overlap[mid + shortLen * 4 + i] + buf[shortLen * 7 + i] * SHORT_WINDOWS[windowShape][shortLen - 1 - i] + buf[shortLen * 8 + i] * SHORT_WINDOWS[windowShape][i]
                    i++
                }

                //window the second half and save as overlap for next frame
                i = 0
                while (i < shortLen) {
                    if (i >= trans) overlap[mid + 4 * shortLen + i - length] =
                        buf[shortLen * 7 + i] * SHORT_WINDOWS[windowShape][shortLen - 1 - i] + buf[shortLen * 8 + i] * SHORT_WINDOWS[windowShape][i]
                    overlap[mid + 5 * shortLen + i - length] =
                        buf[shortLen * 9 + i] * SHORT_WINDOWS[windowShape][shortLen - 1 - i] + buf[shortLen * 10 + i] * SHORT_WINDOWS[windowShape][i]
                    overlap[mid + 6 * shortLen + i - length] =
                        buf[shortLen * 11 + i] * SHORT_WINDOWS[windowShape][shortLen - 1 - i] + buf[shortLen * 12 + i] * SHORT_WINDOWS[windowShape][i]
                    overlap[mid + 7 * shortLen + i - length] =
                        buf[shortLen * 13 + i] * SHORT_WINDOWS[windowShape][shortLen - 1 - i] + buf[shortLen * 14 + i] * SHORT_WINDOWS[windowShape][i]
                    overlap[mid + 8 * shortLen + i - length] =
                        buf[shortLen * 15 + i] * SHORT_WINDOWS[windowShape][shortLen - 1 - i]
                    i++
                }
                i = 0
                while (i < mid) {
                    overlap[mid + shortLen + i] = 0f
                    i++
                }
            }

            WindowSequence.LONG_STOP_SEQUENCE -> {
                mdctLong.process(`in`, 0, buf, 0)
                //add second half output of previous frame to windowed output of current frame
                //construct first half window using padding with 1's and 0's
                i = 0
                while (i < mid) {
                    out[i] = overlap[i]
                    i++
                }
                i = 0
                while (i < shortLen) {
                    out[mid + i] = overlap[mid + i] + buf[mid + i] * SHORT_WINDOWS[windowShapePrev][i]
                    i++
                }
                i = 0
                while (i < mid) {
                    out[mid + shortLen + i] = overlap[mid + shortLen + i] + buf[mid + shortLen + i]
                    i++
                }
                //window the second half and save as overlap for next frame
                i = 0
                while (i < length) {
                    overlap[i] = buf[length + i] * LONG_WINDOWS[windowShape][length - 1 - i]
                    i++
                }
            }

            else -> {}
        }
    }

    //only for LTP: no overlapping, no short blocks
    fun processLTP(
        windowSequence: WindowSequence?,
        windowShape: Int,
        windowShapePrev: Int,
        `in`: FloatArray,
        out: FloatArray
    ) {
        var i: Int
        when (windowSequence) {
            WindowSequence.ONLY_LONG_SEQUENCE -> {
                i = length - 1
                while (i >= 0) {
                    buf[i] = `in`[i] * LONG_WINDOWS[windowShapePrev][i]
                    buf[i + length] = `in`[i + length] * LONG_WINDOWS[windowShape][length - 1 - i]
                    i--
                }
            }

            WindowSequence.LONG_START_SEQUENCE -> {
                i = 0
                while (i < length) {
                    buf[i] = `in`[i] * LONG_WINDOWS[windowShapePrev][i]
                    i++
                }
                i = 0
                while (i < mid) {
                    buf[i + length] = `in`[i + length]
                    i++
                }
                i = 0
                while (i < shortLen) {
                    buf[i + length + mid] = `in`[i + length + mid] * SHORT_WINDOWS[windowShape][shortLen - 1 - i]
                    i++
                }
                i = 0
                while (i < mid) {
                    buf[i + length + mid + shortLen] = 0f
                    i++
                }
            }

            WindowSequence.LONG_STOP_SEQUENCE -> {
                i = 0
                while (i < mid) {
                    buf[i] = 0f
                    i++
                }
                i = 0
                while (i < shortLen) {
                    buf[i + mid] = `in`[i + mid] * SHORT_WINDOWS[windowShapePrev][i]
                    i++
                }
                i = 0
                while (i < mid) {
                    buf[i + mid + shortLen] = `in`[i + mid + shortLen]
                    i++
                }
                i = 0
                while (i < length) {
                    buf[i + length] = `in`[i + length] * LONG_WINDOWS[windowShape][length - 1 - i]
                    i++
                }
            }

            else -> { }
        }
        mdctLong.processForward(buf, out)
    }

    fun getOverlap(channel: Int): FloatArray {
        return overlaps[channel]
    }
}
