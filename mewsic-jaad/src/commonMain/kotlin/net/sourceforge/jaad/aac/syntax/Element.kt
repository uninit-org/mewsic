package net.sourceforge.jaad.aac.syntax
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.SampleFrequency
import net.sourceforge.jaad.aac.sbr.SBR

abstract class Element : Constants {
    var elementInstanceTag = 0
        private set
    private var sbr: SBR? = null
    @Throws(AACException::class)
    protected fun readElementInstanceTag(`in`: BitStream) {
        elementInstanceTag = `in`.readBits(4)
    }

    @Throws(AACException::class)
    fun decodeSBR(
        `in`: BitStream?,
        sf: SampleFrequency?,
        count: Int,
        stereo: Boolean,
        crc: Boolean,
        downSampled: Boolean,
        smallFrames: Boolean
    ) {
        if (sbr == null) sbr = SBR(smallFrames, elementInstanceTag == Constants.ELEMENT_CPE, sf!!, downSampled)
        sbr!!.decode(`in`!!, count)
    }

    val isSBRPresent: Boolean
        get() = sbr != null
    val sBR: SBR?
        get() = sbr
}
