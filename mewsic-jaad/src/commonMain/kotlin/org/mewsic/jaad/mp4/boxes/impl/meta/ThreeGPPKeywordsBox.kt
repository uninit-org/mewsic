package net.sourceforge.jaad.mp4.boxes.impl.meta

import net.sourceforge.jaad.mp4.MP4InputStream

class ThreeGPPKeywordsBox : ThreeGPPMetadataBox("3GPP Keywords Box") {
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
