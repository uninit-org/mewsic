package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

class SampleToChunkBox : FullBox("Sample To Chunk Box") {
    lateinit var firstChunks: LongArray
        private set
    lateinit var samplesPerChunk: LongArray
        private set
    lateinit var sampleDescriptionIndex: LongArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4).toInt()
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
