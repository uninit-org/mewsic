package net.sourceforge.jaad.mp4.boxes.impl.meta
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class ThreeGPPKeywordsBox : net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox("3GPP Keywords Box") {
    lateinit var keywords: Array<String?>
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        decodeCommon(`in`)
        val count: Int = `in`.read()
        keywords = arrayOfNulls(count)
        var len: Int
        for (i in 0 until count) {
            len = `in`.read()
            keywords[i] = `in`.readUTFString(len)
        }
    }
}
