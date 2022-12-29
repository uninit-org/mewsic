package org.mewsic.audioplayer.source

import org.mewsic.audioplayer.AudioChunk

interface AudioSource : Iterator<AudioChunk> {
    fun seekTo(seconds: Float)
    fun seekToPercent(percent: Float) = seekTo(percent * metadata.duration)

    val metadata: AudioMetadata
}
