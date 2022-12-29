package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

class AppleLosslessBox : FullBox("Apple Lossless Box") {
    var maxSamplePerFrame: Long = 0
        private set
    var maxCodedFrameSize: Long = 0
        private set
    var bitRate: Long = 0
        private set
    var sampleRate: Long = 0
        private set
    var sampleSize = 0
        private set
    var historyMult = 0
        private set
    var initialHistory = 0
        private set
    private var kModifier = 0
    var channels = 0
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        maxSamplePerFrame = `in`.readBytes(4)
        `in`.skipBytes(1) //?
        sampleSize = `in`.read()
        historyMult = `in`.read()
        initialHistory = `in`.read()
        kModifier = `in`.read()
        channels = `in`.read()
        `in`.skipBytes(2) //?
        maxCodedFrameSize = `in`.readBytes(4)
        bitRate = `in`.readBytes(4)
        sampleRate = `in`.readBytes(4)
    }

    fun getkModifier(): Int {
        return kModifier
    }
}
