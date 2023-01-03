package net.sourceforge.jaad.mp4.api.codec

import net.sourceforge.jaad.mp4.api.DecoderInfo
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox
import net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.EAC3SpecificBox

class EAC3DecoderInfo(box: CodecSpecificBox) : DecoderInfo() {
    private val box: EAC3SpecificBox
    val independentSubstreams: Array<IndependentSubstream?>

    init {
        this.box = box as EAC3SpecificBox
        independentSubstreams = arrayOfNulls(this.box.independentSubstreamCount)
        for (i in independentSubstreams.indices) {
            independentSubstreams[i] = IndependentSubstream(i)
        }
    }

    val dataRate: Int
        get() = box.dataRate

    inner class IndependentSubstream constructor(private val index: Int) {
        val dependentSubstreams: Array<DependentSubstream>

        init {
            val loc: Int = box.dependentSubstreamLocation.get(index)
            val list: MutableList<DependentSubstream> = ArrayList<DependentSubstream>()
            for (i in 0..8) {
                if (loc shr 8 - i and 1 == 1) list.add(DependentSubstream.values()[i])
            }
            dependentSubstreams = list.toTypedArray()
        }

        val fscod: Int
            get() = box.fscods.get(index)
        val bsid: Int
            get() = box.bsids.get(index)
        val bsmod: Int
            get() = box.bsmods.get(index)
        val acmod: Int
            get() = box.acmods.get(index)
        val isLfeon: Boolean
            get() = box.lfeons.get(index)
    }

    enum class DependentSubstream {
        LC_RC_PAIR, LRS_RRS_PAIR, CS, TS, LSD_RSD_PAIR, LW_RW_PAIR, LVH_RVH_PAIR, CVH, LFE2
    }
}
