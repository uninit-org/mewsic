package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.boxes.FullBox

class SampleToChunkBox : FullBox("Sample To Chunk Box") {
    var firstChunks: LongArray
        private set
    var samplesPerChunk: LongArray
        private set
    var sampleDescriptionIndex: LongArray
        private set

    @Throws(Exception::class)
    override override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4) as Int
        firstChunks = LongArray(entryCount)
        samplesPerChunk = LongArray(entryCount)
        sampleDescriptionIndex = LongArray(entryCount)
        for (i in 0 until entryCount) {
            firstChunks[i] = `in`.readBytes(4)
            samplesPerChunk[i] = `in`.readBytes(4)
            sampleDescriptionIndex[i] = `in`.readBytes(4)
        }
    }
}
