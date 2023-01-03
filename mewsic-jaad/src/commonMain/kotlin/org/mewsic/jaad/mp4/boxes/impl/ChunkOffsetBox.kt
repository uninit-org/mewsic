package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import net.sourceforge.jaad.mp4.boxes.FullBox

class ChunkOffsetBox : FullBox("Chunk Offset Box") {
    lateinit var chunks: LongArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val len = if (type == BoxTypes.CHUNK_LARGE_OFFSET_BOX) 8 else 4
        val entryCount = `in`.readBytes(4).toInt()
        chunks = LongArray(entryCount)
        for (i in 0 until entryCount) {
            chunks[i] = `in`.readBytes(len)
        }
    }
}
