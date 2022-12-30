package net.sourceforge.jaad.aac.syntax
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.aac.AACException

internal class DSE : Element() {
    private lateinit var dataStreamBytes: ByteArray
    @Throws(AACException::class)
    override fun decode(`in`: BitStream) {
        val byteAlign = `in`.readBool()
        var count = `in`.readBits(8)
        if (count == 255) count += `in`.readBits(8)
        if (byteAlign) `in`.byteAlign()
        dataStreamBytes = ByteArray(count)
        for (i in 0 until count) {
            dataStreamBytes[i] = `in`.readBits(8).toByte()
        }
    }
}
