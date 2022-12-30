package net.sourceforge.jaad.aac.syntax
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.DecoderConfig

internal class SCE_LFE(frameLength: Int) : Element() {
    val iCStream: ICStream

    init {
        iCStream = ICStream(frameLength)
    }

    @Throws(AACException::class)
    override fun decode(`in`: BitStream?, conf: DecoderConfig?) {
        readElementInstanceTag(`in`!!)
        iCStream.decode(`in`, false, conf!!)
    }
}
