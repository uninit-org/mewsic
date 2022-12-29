package org.mewsic.audioplayer

class AudioChunk(
    val channels: Int,
    val sampleRate: Int,
    val samples: Int,
) {
    var audioByChannel = Array(channels) { FloatArray(samples) }
        private set

    fun ensureStereo() {
        if (channels < 2) {
            val mono = audioByChannel[0]
            audioByChannel = arrayOf(mono, mono)
        }
        if (channels > 2) {
            audioByChannel = audioByChannel.copyOfRange(0, 2)
        }
    }
}
