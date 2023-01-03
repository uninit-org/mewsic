package org.mewsic.jaad.aac.syntax

import org.mewsic.jaad.aac.AACException
import org.mewsic.jaad.aac.DecoderConfig

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
