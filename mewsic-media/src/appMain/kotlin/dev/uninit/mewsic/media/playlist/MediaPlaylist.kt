package dev.uninit.mewsic.media.playlist

import dev.uninit.mewsic.media.track.MediaTrack

sealed interface MediaPlaylist {
    val name: String
    val tracks: List<MediaTrack>

    // These two should make changes in memory only
    fun addTrack(track: MediaTrack)
    fun removeTrack(track: MediaTrack)

    // This should save the playlist
    suspend fun save()
}
