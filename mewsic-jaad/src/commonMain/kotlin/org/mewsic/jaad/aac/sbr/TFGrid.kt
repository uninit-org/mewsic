package org.mewsic.jaad.aac.sbr

import org.mewsic.jaad.aac.sbr.Constants.Companion.FIXFIX
import org.mewsic.jaad.aac.sbr.Constants.Companion.FIXVAR
import org.mewsic.jaad.aac.sbr.Constants.Companion.VARFIX
import org.mewsic.jaad.aac.sbr.Constants.Companion.VARVAR

internal object TFGrid : Constants {
    /* function constructs new time border vector */ /* first build into temp vector to be able to use previous vector on error */
    fun envelope_time_border_vector(sbr: SBR, ch: Int): Int {
        var l: Int
        var border: Int
        val temp: Int
        val t_E_temp = IntArray(6)
        t_E_temp[0] = sbr.rate * sbr.abs_bord_lead.get(ch)
        t_E_temp[sbr.L_E.get(ch)] = sbr.rate * sbr.abs_bord_trail.get(ch)
        when (sbr.bs_frame_class.get(ch)) {
            FIXFIX -> when (sbr.L_E.get(ch)) {
                4 -> {
                    temp = sbr.numTimeSlots / 4
                    t_E_temp[3] = sbr.rate * 3 * temp
                    t_E_temp[2] = sbr.rate * 2 * temp
                    t_E_temp[1] = sbr.rate * temp
                }

                2 -> t_E_temp[1] = sbr.rate * (sbr.numTimeSlots / 2)
                else -> {}
            }

            FIXVAR -> if (sbr.L_E.get(ch) > 1) {
                var i: Int = sbr.L_E.get(ch)
                border = sbr.abs_bord_trail.get(ch)
                l = 0
                while (l < sbr.L_E.get(ch) - 1) {
                    if (border < sbr.bs_rel_bord.get(ch).get(l)) return 1
                    border -= sbr.bs_rel_bord.get(ch).get(l)
                    t_E_temp[--i] = sbr.rate * border
                    l++
                }
            }

            VARFIX -> if (sbr.L_E.get(ch) > 1) {
                var i = 1
                border = sbr.abs_bord_lead.get(ch)
                l = 0
                while (l < sbr.L_E.get(ch) - 1) {
                    border += sbr.bs_rel_bord.get(ch).get(l)
                    if (sbr.rate * border + sbr.tHFAdj > sbr.numTimeSlotsRate + sbr.tHFGen) return 1
                    t_E_temp[i++] = sbr.rate * border
                    l++
                }
            }

            VARVAR -> {
                if (sbr.bs_num_rel_0.get(ch) != 0) {
                    var i = 1
                    border = sbr.abs_bord_lead.get(ch)
                    l = 0
                    while (l < sbr.bs_num_rel_0.get(ch)) {
                        border += sbr.bs_rel_bord_0.get(ch).get(l)
                        if (sbr.rate * border + sbr.tHFAdj > sbr.numTimeSlotsRate + sbr.tHFGen) return 1
                        t_E_temp[i++] = sbr.rate * border
                        l++
                    }
                }
                if (sbr.bs_num_rel_1.get(ch) != 0) {
                    var i: Int = sbr.L_E.get(ch)
                    border = sbr.abs_bord_trail.get(ch)
                    l = 0
                    while (l < sbr.bs_num_rel_1.get(ch)) {
                        if (border < sbr.bs_rel_bord_1.get(ch).get(l)) return 1
                        border -= sbr.bs_rel_bord_1.get(ch).get(l)
                        t_E_temp[--i] = sbr.rate * border
                        l++
                    }
                }
            }
        }

        /* no error occured, we can safely use this t_E vector */l = 0
        while (l < 6) {
            sbr.t_E.get(ch)[l] = t_E_temp[l]
            l++
        }
        return 0
    }

    fun noise_floor_time_border_vector(sbr: SBR, ch: Int) {
        sbr.t_Q.get(ch)[0] = sbr.t_E.get(ch).get(0)
        if (sbr.L_E.get(ch) == 1) {
            sbr.t_Q.get(ch)[1] = sbr.t_E.get(ch).get(1)
            sbr.t_Q.get(ch)[2] = 0
        } else {
            val index = middleBorder(sbr, ch)
            sbr.t_Q.get(ch)[1] = sbr.t_E.get(ch).get(index)
            sbr.t_Q.get(ch)[2] = sbr.t_E.get(ch).get(sbr.L_E.get(ch))
        }
    }

    private fun middleBorder(sbr: SBR, ch: Int): Int {
        var retval = 0
        when (sbr.bs_frame_class.get(ch)) {
            FIXFIX -> retval = sbr.L_E.get(ch) / 2
            VARFIX -> retval =
                if (sbr.bs_pointer.get(ch) == 0) 1 else if (sbr.bs_pointer.get(ch) == 1) sbr.L_E.get(ch) - 1 else sbr.bs_pointer.get(
                    ch
                ) - 1

            FIXVAR, VARVAR -> retval =
                if (sbr.bs_pointer.get(ch) > 1) sbr.L_E.get(ch) + 1 - sbr.bs_pointer.get(ch) else sbr.L_E.get(ch) - 1
        }
        return if (retval > 0) retval else 0
    }
}
