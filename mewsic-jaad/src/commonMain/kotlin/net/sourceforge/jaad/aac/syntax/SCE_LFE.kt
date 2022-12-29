package net.sourceforge.jaad.aac.syntax

import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.DecoderConfig

internal class SCE_LFE(frameLength: Int) : Element() {
    val iCStream: ICStream

    init {
        iCStream = ICStream(frameLength)
    }

    @Throws(AACException::class)
    fun decode(`in`: BitStream?, conf: DecoderConfig?) {
        readElementInstanceTag(`in`!!)
        iCStream.decode(`in`, false, conf!!)
    }
}
