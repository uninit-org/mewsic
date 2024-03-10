package dev.uninit.mewsic.media.playlist

import dev.uninit.mewsic.media.track.MediaTrack

class MixedMediaPlaylist(
    override val name: String,
    override val tracks: MutableList<MediaTrack>
) : MediaPlaylist {
    override fun addTrack(track: MediaTrack) {
        tracks.add(track)
    }

    override fun removeTrack(track: MediaTrack) {
        tracks.remove(track)
    }

    override suspend fun save() {
        // TODO: Implement saving
    }
}
