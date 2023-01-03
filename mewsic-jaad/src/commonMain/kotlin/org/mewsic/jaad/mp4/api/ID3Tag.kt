package org.mewsic.jaad.mp4.api

import org.mewsic.commons.streams.DataInputStream

internal class ID3Tag(`in`: DataInputStream) {
    private val frames: MutableList<ID3Frame>
    private val tag: Int
    private val flags: Int
    private val len: Int

    init {
        frames = ArrayList<ID3Frame>()

        //id3v2 header
        tag = `in`.read().toInt() shl 16 or (`in`.read().toInt() shl 8) or `in`.read().toInt() //'ID3'
        val majorVersion: Byte = `in`.read()
        `in`.read() //revision
        flags = `in`.read().toInt()
        len = readSynch(`in`)
        if (tag == ID3_TAG && majorVersion <= SUPPORTED_VERSION) {
            if (flags and 0x40 == 0x40) {
                //extended header; TODO: parse
                val extSize = readSynch(`in`)
                `in`.skip((extSize - 6).toLong())
            }

            //read all id3 frames
            var left = len
            var frame: ID3Frame
            while (left > 0) {
                frame = ID3Frame(`in`)
                frames.add(frame)
                left -= frame.size.toInt()
            }
        }
    }

    fun getFrames(): List<ID3Frame> {
        return frames.toList()
    }

    companion object {
        private const val ID3_TAG = 4801587 //'ID3'
        private const val SUPPORTED_VERSION = 4 //id3v2.4

        @Throws(Exception::class)
        fun readSynch(`in`: DataInputStream): Int {
            var x = 0
            for (i in 0..3) {
                x = x or (`in`.read().toInt() and 0x7F)
            }
            return x
        }
    }
}
