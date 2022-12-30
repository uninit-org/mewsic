package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.boxes.BoxTypes

class ChunkOffsetBox : FullBox("Chunk Offset Box") {
    var chunks: LongArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val len = if (type === BoxTypes.CHUNK_LARGE_OFFSET_BOX) 8 else 4
        val entryCount = `in`.readBytes(4) as Int
        chunks = LongArray(entryCount)
        for (i in 0 until entryCount) {
            chunks[i] = `in`.readBytes(len)
        }
    }
}
