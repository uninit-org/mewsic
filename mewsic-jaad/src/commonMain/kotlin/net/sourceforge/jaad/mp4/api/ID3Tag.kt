package net.sourceforge.jaad.mp4.api

internal class ID3Tag(`in`: java.io.DataInputStream) {
    private val frames: MutableList<net.sourceforge.jaad.mp4.api.ID3Frame>
    private val tag: Int
    private val flags: Int
    private val len: Int

    init {
        frames = java.util.ArrayList<net.sourceforge.jaad.mp4.api.ID3Frame>()

        //id3v2 header
        tag = `in`.read() shl 16 or (`in`.read() shl 8) or `in`.read() //'ID3'
        val majorVersion: Int = `in`.read()
        `in`.read() //revision
        flags = `in`.read()
        len = readSynch(`in`)
        if (tag == ID3_TAG && majorVersion <= SUPPORTED_VERSION) {
            if (flags and 0x40 == 0x40) {
                //extended header; TODO: parse
                val extSize = readSynch(`in`)
                `in`.skipBytes(extSize - 6)
            }

            //read all id3 frames
            var left = len
            var frame: net.sourceforge.jaad.mp4.api.ID3Frame
            while (left > 0) {
                frame = net.sourceforge.jaad.mp4.api.ID3Frame(`in`)
                frames.add(frame)
                left -= frame.getSize().toInt()
            }
        }
    }

    fun getFrames(): List<net.sourceforge.jaad.mp4.api.ID3Frame> {
        return java.util.Collections.unmodifiableList<net.sourceforge.jaad.mp4.api.ID3Frame>(frames)
    }

    companion object {
        private const val ID3_TAG = 4801587 //'ID3'
        private const val SUPPORTED_VERSION = 4 //id3v2.4
        @Throws(java.io.IOException::class)
        fun readSynch(`in`: java.io.DataInputStream): Int {
            var x = 0
            for (i in 0..3) {
                x = x or (`in`.read() and 0x7F)
            }
            return x
        }
    }
}
