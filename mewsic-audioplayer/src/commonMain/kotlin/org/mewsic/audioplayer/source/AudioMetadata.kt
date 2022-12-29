package org.mewsic.audioplayer.source

interface AudioMetadata {
    val title: String
    val artist: String
    val album: String?
    val duration: Float  // in seconds
}
