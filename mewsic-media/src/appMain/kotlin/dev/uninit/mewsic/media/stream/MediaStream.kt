package dev.uninit.mewsic.media.stream

interface MediaStream {
    val offset: Long
    var gain: Float

    // Position in samples
    suspend fun seekTo(position: Long)

    // Should fill the buffer with length/8 float interleaved samples at 44.1kHz
    suspend fun copyInto(buffer: ByteArray, offset: Int, length: Int): Int

    fun dispose()
}
